package poc

import org.apache.spark.sql.functions.{col, struct}
import org.apache.spark.sql.types.StructType
import org.apache.spark.sql.{DataFrame, SparkSession}
import za.co.absa.abris.avro.functions.to_avro
import za.co.absa.abris.config.{AbrisConfig, ToAvroConfig}

object StructureStreamingToAvroConfluentAvro extends App {

  val abrisConfig: ToAvroConfig = AbrisConfig
    .toConfluentAvro
    .downloadSchemaByLatestVersion
    .andTopicNameStrategy("spark-poc")
    .usingSchemaRegistry("http://localhost:8070")

  val spark = SparkSession
    .builder
    .appName("Simple Application")
    .master("local[*]")
    .config("spark.driver.bindAddress", "127.0.0.1")
    .getOrCreate()

  import spark.implicits._

  val dataframe = Seq(("foo2"), ("bar2")).toDF("f1")
  val dataframeWithNullRemoved = setNullableStateForAllColumns(dataframe, false)
  val formattedForKafka = dataframeWithNullRemoved.select(struct(col("f1")).as("value_struct"))
  val formattedForKafkaDataframeWithNullRemoved = setNullableStateForAllColumns(formattedForKafka, false)
  val seriliazedAsAvro = formattedForKafkaDataframeWithNullRemoved.select(to_avro(col("value_struct"), abrisConfig).as("value"))

  seriliazedAsAvro
    .write
    .format("kafka")
    .option("kafka.bootstrap.servers", "localhost:29092")
    .option("topic", "spark-poc")
    .save()

  spark.stop()

  def setNullableStateForAllColumns(df: DataFrame, nullable: Boolean) : DataFrame = {
    df.sqlContext.createDataFrame(df.rdd, StructType(df.schema.map(_.copy(nullable = nullable))))
  }
}
