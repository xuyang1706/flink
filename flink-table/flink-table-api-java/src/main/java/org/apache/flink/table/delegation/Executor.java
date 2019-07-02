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

package org.apache.flink.table.delegation;

import org.apache.flink.annotation.Internal;
import org.apache.flink.api.common.JobExecutionResult;
import org.apache.flink.api.dag.Transformation;
import org.apache.flink.table.api.TableEnvironment;

import java.util.List;

/**
 * It enables execution of a {@link Transformation}s graph generated by
 * {@link Planner}.
 *
 * <p>This uncouples the {@link TableEnvironment} from any given runtime.
 */
@Internal
public interface Executor {

	/**
	 * Applies all given transformations. This should not run the transformations already, but just apply for
	 * future execution via {@link #execute(String)}
	 *
	 * @param transformations list of transformations to apply
	 */
	void apply(List<Transformation<?>> transformations);

	/**
	 * Executes all the previously applied transformations via {@link #apply(List)}.
	 *
	 * @param jobName what should be the name of the job
	 * @return The result of the job execution, containing elapsed time and accumulators.
	 * @throws Exception which occurs during job execution.
	 */
	JobExecutionResult execute(String jobName) throws Exception;
}
