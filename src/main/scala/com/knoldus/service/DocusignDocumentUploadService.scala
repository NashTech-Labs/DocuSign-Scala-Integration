package com.knoldus.service

import java.io.InputStream
import java.util.Base64

import com.docusign.esign.api.TemplatesApi
import com.docusign.esign.client.ApiClient
import com.docusign.esign.client.auth.OAuth
import com.docusign.esign.model.{Document, EnvelopeTemplate, EnvelopeTemplateDefinition}
import com.knoldus.app.DocuSignCredentials
import com.knoldus.util.LoggerHelper

import scala.collection.JavaConverters._
import scala.util.{Failure, Success, Try}

case class DocuSignTemplateId(templateId: String)

trait DocusignDocumentUploadService extends LoggerHelper {
  val apiClient: ApiClient
  val templatesApi: TemplatesApi

  def createAndSetJWTAcessToken(
                                 clientCredentials: DocuSignCredentials): Try[OAuth.OAuthToken] = {
    Try {
      apiClient.setOAuthBasePath(clientCredentials.dsAuthServer)
      val oAuthToken = apiClient.requestJWTUserToken(
        clientCredentials.dsClientId,
        clientCredentials.dsImpersonatedId,
        List(OAuth.Scope_SIGNATURE).asJava,
        clientCredentials.dsPrivateKey.getBytes,
        clientCredentials.tokenExpirationInSeconds)
      apiClient.setAccessToken(oAuthToken.getAccessToken, oAuthToken.getExpiresIn)
      apiClient.setBasePath(clientCredentials.basePath)
      oAuthToken
    }
  }

  def uploadDocOnDocusign(
                           clientCredentials: DocuSignCredentials,
                           documentName: String,
                           documentExtension: String,
                           documentInputStream: InputStream): Try[DocuSignTemplateId] = {
    val apiResponse = for {
      _ <- createAndSetJWTAcessToken(clientCredentials)
    } yield {
      val templateId = templatesApi.createTemplate(
        clientCredentials.accountId,
        createTemplate(documentName, documentExtension, documentInputStream)).getTemplateId
      DocuSignTemplateId(templateId)
    }

    apiResponse match {
      case value@Success(id) =>
        logger.info(s"Successfully uploaded documents and got template id ::: $id ")
        value
      case value@Failure(ex: Throwable) =>
        logger.error("Unable to upload Documents on Docusign server :: ", ex)
        value
    }
  }

  private def createDocument(docBase64: String, documentName: String, documentExtension: String): Document = {
    val document = new Document()
    document.setDocumentBase64(docBase64)
    document.setName(documentName)
    document.setFileExtension(documentExtension)
    document.setDocumentId("1")
    document
  }

  private def createTemplate(
                              documentName: String,
                              documentExtension: String,
                              inputStream: InputStream): EnvelopeTemplate = {
    val docBase64 = new String(Base64.getEncoder.encode(readFileContentFromS3Object(inputStream)))
    val document = createDocument(docBase64, documentName, documentExtension)
    val envelopeTemplateDefinition = new EnvelopeTemplateDefinition()
    envelopeTemplateDefinition.setDescription(s"${documentName} template created via the API")
    envelopeTemplateDefinition.setName(documentName)
    val template = new EnvelopeTemplate()
    template.setDocuments(List(document).asJava)
    template.setEnvelopeTemplateDefinition(envelopeTemplateDefinition)
    template.setStatus("created")
    template
  }

  private def readFileContentFromS3Object(inputStream: InputStream): Array[Byte] = {
    Iterator continually inputStream.read takeWhile (-1 !=) map (_.toByte) toArray
  }

}
