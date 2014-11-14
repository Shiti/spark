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

package org.apache.spark.ml.feature

import org.apache.spark.annotation.AlphaComponent
import org.apache.spark.ml.UnaryTransformer
import org.apache.spark.ml.param.{IntParam, ParamMap}
import org.apache.spark.mllib.feature
import org.apache.spark.mllib.linalg.Vector

/**
 * :: AlphaComponent ::
 * Maps a sequence of terms to their term frequencies using the hashing trick.
 */
@AlphaComponent
class HashingTF extends UnaryTransformer[Iterable[_], Vector, HashingTF] {

  /** number of features */
  val numFeatures = new IntParam(this, "numFeatures", "number of features", Some(1 << 18))
  def setNumFeatures(value: Int) = set(numFeatures, value)
  def getNumFeatures: Int = get(numFeatures)

  override protected def createTransformFunc(paramMap: ParamMap): Iterable[_] => Vector = {
    val hashingTF = new feature.HashingTF(paramMap(numFeatures))
    hashingTF.transform
  }
}