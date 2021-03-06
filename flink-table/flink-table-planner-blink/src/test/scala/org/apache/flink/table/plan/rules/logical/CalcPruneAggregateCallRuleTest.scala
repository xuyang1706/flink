/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.flink.table.plan.rules.logical

import org.apache.flink.table.calcite.CalciteConfig
import org.apache.flink.table.plan.optimize.program.{FlinkBatchProgram, FlinkHepRuleSetProgramBuilder, HEP_RULES_EXECUTION_TYPE}

import org.apache.calcite.plan.hep.HepMatchOrder
import org.apache.calcite.rel.rules.{FilterCalcMergeRule, FilterToCalcRule, ProjectCalcMergeRule, ProjectToCalcRule}
import org.apache.calcite.tools.RuleSets

/**
  * Test for [[PruneAggregateCallRule]]#CALC_ON_AGGREGATE.
  */
class CalcPruneAggregateCallRuleTest extends PruneAggregateCallRuleTestBase {

  override def setup(): Unit = {
    super.setup()
    util.buildBatchProgram(FlinkBatchProgram.LOGICAL)

    val programs = util.getTableEnv.getConfig.getCalciteConfig.getBatchProgram.get
    programs.addLast("rules",
      FlinkHepRuleSetProgramBuilder.newBuilder
      .setHepRulesExecutionType(HEP_RULES_EXECUTION_TYPE.RULE_COLLECTION)
      .setHepMatchOrder(HepMatchOrder.BOTTOM_UP)
      .add(RuleSets.ofList(
        AggregateReduceGroupingRule.INSTANCE,
        FilterCalcMergeRule.INSTANCE,
        ProjectCalcMergeRule.INSTANCE,
        FilterToCalcRule.INSTANCE,
        ProjectToCalcRule.INSTANCE,
        FlinkCalcMergeRule.INSTANCE,
        PruneAggregateCallRule.CALC_ON_AGGREGATE)
      ).build())

    val calciteConfig = CalciteConfig.createBuilder(util.tableEnv.getConfig.getCalciteConfig)
      .replaceBatchProgram(programs).build()
    util.tableEnv.getConfig.setCalciteConfig(calciteConfig)
  }
}
