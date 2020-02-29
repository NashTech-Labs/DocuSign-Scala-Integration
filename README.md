DocuSign-Scala-Integration
=========

## Overview ##

In this repo, we will explore & Integrate the DocuSign server APIs with Scala service. To upload a document from the local filesystem and get a digital signature.

## Goals ##

1. Fetch a document in a stream from the local filesystem.
2. Upload documents to DocuSign server in template form.
3. After the successful signing, we will get a template id.

## Specifications ##

ScalaVersion := 2.12.8

## Pre-Requisites before running the project ##
In order to run this project. We have to fill the all required DocuSign credentials in `application.conf` file.

### 4 Running the Project ###

Start/Run the DocuSign-Scala-Integration

`sbt run`