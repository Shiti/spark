/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.spark.ml.util

import scala.collection.immutable.HashMap

import org.apache.spark.annotation.Experimental
import org.apache.spark.ml.attribute.{Attribute, AttributeGroup, BinaryAttribute, NominalAttribute,
  NumericAttribute}
import org.apache.spark.sql.types.StructField


/**
 * :: Experimental ::
 *
 * Helper utilities for tree-based algorithms
 */
@Experimental
object MetadataUtils {

  /**
   * Examine a schema to identify the number of classes in a label column.
   * Returns None if the number of labels is not specified, or if the label column is continuous.
   */
  def getNumClasses(labelSchema: StructField): Option[Int] = {
    Attribute.fromStructField(labelSchema) match {
      case numAttr: NumericAttribute => None
      case binAttr: BinaryAttribute => Some(2)
      case nomAttr: NominalAttribute => nomAttr.getNumValues
    }
  }

  /**
   * Examine a schema to identify categorical (Binary and Nominal) features.
   *
   * @param featuresSchema  Schema of the features column.
   *                        If a feature does not have metadata, it is assumed to be continuous.
   *                        If a feature is Nominal, then it must have the number of values
   *                        specified.
   * @return  Map: feature index --> number of categories.
   *          The map's set of keys will be the set of categorical feature indices.
   */
  def getCategoricalFeatures(featuresSchema: StructField): Map[Int, Int] = {
    val metadata = AttributeGroup.fromStructField(featuresSchema)
    if (metadata.attributes.isEmpty) {
      HashMap.empty[Int, Int]
    } else {
      metadata.attributes.get.zipWithIndex.flatMap { case (attr, idx) =>
        if (attr == null) {
          Iterator()
        } else {
          attr match {
            case numAttr: NumericAttribute => Iterator()
            case binAttr: BinaryAttribute => Iterator(idx -> 2)
            case nomAttr: NominalAttribute =>
              nomAttr.getNumValues match {
                case Some(numValues: Int) => Iterator(idx -> numValues)
                case None => throw new IllegalArgumentException(s"Feature $idx is marked as" +
                  " Nominal (categorical), but it does not have the number of values specified.")
              }
          }
        }
      }.toMap
    }
  }

}
