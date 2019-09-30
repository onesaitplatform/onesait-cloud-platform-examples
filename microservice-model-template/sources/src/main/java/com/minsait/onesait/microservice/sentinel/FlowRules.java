/**
 * Copyright Indra Soluciones Tecnologías de la Información, S.L.U.
 * 2013-2019 SPAIN
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *      http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.minsait.onesait.microservice.sentinel;

import java.util.ArrayList;
import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.alibaba.csp.sentinel.slots.block.RuleConstant;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRule;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRuleManager;

@Configuration
public class FlowRules {

	private static final String RESOURCE_PRINCIPAL = "principalInfo";

	@Bean
	private static void initFlowQpsRule() {
		final List<FlowRule> rules = new ArrayList<>();
		final FlowRule rule1 = new FlowRule();
		rule1.setResource(RESOURCE_PRINCIPAL);
		rule1.setCount(2);
		rule1.setGrade(RuleConstant.FLOW_GRADE_QPS);
		rule1.setControlBehavior(RuleConstant.CONTROL_BEHAVIOR_DEFAULT);
		rules.add(rule1);
		FlowRuleManager.loadRules(rules);
	}

}
