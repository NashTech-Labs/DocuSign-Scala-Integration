package com.knoldus.app

import java.io.InputStream

import com.docusign.esign.api.TemplatesApi
import com.docusign.esign.client.ApiClient
import com.knoldus.service.DocusignDocumentUploadService
import com.typesafe.config.ConfigFactory

import scala.util.{Failure, Success}

case class DocuSignCredentials(
  dsClientId: String,
  dsImpersonatedId: String,
  dsAuthServer: String,
  accountId: String,
  tokenExpirationInSeconds: Int,
  dsPrivateKey: String,
  basePath: String)

/**
 * This Docusign app is used to upload a document on docusign server for signing purpose
 */
object DocusignApp extends App with DocusignDocumentUploadService {

  override val apiClient: ApiClient = new ApiClient()
  override val templatesApi: TemplatesApi = new TemplatesApi(apiClient)
  val config = ConfigFactory.load().getObject("knoldus").toConfig
  val credentials = DocuSignCredentials(
    config.getString("ds_client_id"),
    config.getString("ds_impersonated_id"),
    config.getString("ds_auth_server"),
    config.getString("account_id"),
    config.getInt("token_expiration_in_seconds"),
    config.getString("ds_private_key"),
    config.getString("base_path"))

  def inputStream: InputStream = getClass.getResourceAsStream("/" +
    "Specification_Tenant_ Document_Upload_project.pdf")

  uploadDocOnDocusign(credentials, "mydoc", "pdf", inputStream) match {
    case Success(value) => logger.info(s"Successfully uploaded a document on DocuSign server and templateId is ${value.templateId}")
    case Failure(ex) => logger.error(s"Unable to upload a document on DocuSign server due to ${ex.getMessage} ")
  }

}
