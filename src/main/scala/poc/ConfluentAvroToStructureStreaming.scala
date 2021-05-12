package poc

import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.functions.col
import za.co.absa.abris.avro.functions.from_avro
import za.co.absa.abris.config.{AbrisConfig, FromAvroConfig}

object ConfluentAvroToStructureStreaming extends App {

  val abrisConfig: FromAvroConfig = AbrisConfig
    .fromConfluentAvro
    .downloadReaderSchemaByLatestVersion
    .andTopicNameStrategy("spark-poc")
    .usingSchemaRegistry("http://localhost:8070")

  val spark = SparkSession
    .builder
    .appName("Simple Application")
    .master("local[*]")
    .config("spark.driver.bindAddress", "127.0.0.1")
    .getOrCreate()

  val df = spark
    .readStream
    .format("kafka")
    .option("kafka.bootstrap.servers", "localhost:29092")
    .option("startingOffsets", "earliest")
    .option("subscribe", "spark-poc")
    .load()

  val deserializedAvro = df.select(from_avro(col("value"), abrisConfig).as("data"))
    .select(col("data.*"))

  deserializedAvro.writeStream
    .outputMode("append")
    .format("console")
    .start()
    .awaitTermination()

}
