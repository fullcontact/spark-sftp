package com.springml.spark.sftp


import org.apache.spark.rdd.RDD
import org.apache.spark.sql.{DataFrame, Row, SQLContext}
import org.apache.spark.sql.sources.{BaseRelation, TableScan}
import org.apache.spark.sql.types.StructType

/**
 * Abstract relation class for reading data from file
 */
case class DatasetRelation(
    fileLocation: String,
    fileType: String,
    inferSchema: String,
    header: String,
    delimiter: String,
    quote: String,
    escape: String,
    multiLine: String,
    rowTag: String,
    customSchema: StructType,
    sqlContext: SQLContext) extends BaseRelation with TableScan {

    val df = read()

    private def read(): DataFrame = {
      var dataframeReader = sqlContext.read
      if (customSchema != null) {
        dataframeReader = dataframeReader.schema(customSchema)
      }

      var df: DataFrame = null

      df = fileType match {
        case "txt" => dataframeReader.format("text").load(fileLocation)
        case "xml" => dataframeReader.format(constants.xmlClass)
          .option(constants.xmlRowTag, rowTag)
          .load(fileLocation)
        case "csv" => dataframeReader.
          option("header", header).
          option("delimiter", delimiter).
          option("quote", quote).
          option("escape", escape).
          option("multiLine", multiLine).
          option("inferSchema", inferSchema).
          csv(fileLocation)
        case "json" => dataframeReader.
          json(fileLocation)
        case _ => dataframeReader.format(fileType).load(fileLocation)
      }
     df
    }

    override def schema: StructType = {
      df.schema
    }

    override def buildScan(): RDD[Row] = {
      df.rdd
    }

}
