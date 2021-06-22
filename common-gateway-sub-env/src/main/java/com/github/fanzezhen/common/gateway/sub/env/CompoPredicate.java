/*
 *
 * Copyright 2013 Netflix, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package com.github.fanzezhen.common.gateway.sub.env;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.Lists;
import com.netflix.loadbalancer.AbstractServerPredicate;
import com.netflix.loadbalancer.PredicateKey;
import com.netflix.loadbalancer.Server;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;
import java.util.List;

/**
 * A predicate that is composed from one or more predicates in "AND" relationship.
 * It also has the functionality of "fallback" to one of more different predicates.
 * If the primary predicate yield too few filtered servers from the {@link #getEligibleServers(List, Object)}
 * API, it will try the fallback predicates one by one, until the number of filtered servers
 * exceeds certain number threshold or percentage threshold.
 *
 * @author awang
 */
public class CompoPredicate extends AbstractServerPredicate {

	private static Logger logger = LoggerFactory.getLogger(CompoPredicate.class);

	private AbstractServerPredicate delegate;

	private List<AbstractServerPredicate> fallbacks = Lists.newArrayList();

	private int minimalFilteredServers = 1;

	private float minimalFilteredPercentage = 0;

	private String clientName;

	public static Builder withPredicate(String clientName, AbstractServerPredicate primaryPredicate) {
		return new Builder(clientName, primaryPredicate);
	}

	public static Builder withPredicates(String clientName, AbstractServerPredicate... primaryPredicates) {
		return new Builder(clientName, primaryPredicates);
	}

	@Override
	public boolean apply(PredicateKey input) {
		return delegate.apply(input);
	}

	/**
	 * Get the filtered servers from primary predicate, and if the number of the filtered servers
	 * are not enough, trying the fallback predicates
	 */
	@Override
	public List<Server> getEligibleServers(List<Server> servers, Object loadBalancerKey) {
		LbKey lbKey = (LbKey) loadBalancerKey;
		String subEnv = lbKey.getSubEnv();
		boolean strictMode = lbKey.isStrictMode();
		if (logger.isDebugEnabled()) {
			logger.debug("{} lb key {}", this.clientName, loadBalancerKey);
		}
		List<Server> result = super.getEligibleServers(servers, subEnv);
		if (!strictMode) {
			Iterator<AbstractServerPredicate> i = fallbacks.iterator();
			while (!(result.size() >= minimalFilteredServers && result.size() > (int) (servers.size() * minimalFilteredPercentage))
					&& i.hasNext()) {
				AbstractServerPredicate predicate = i.next();
				result = predicate.getEligibleServers(servers, subEnv);
			}
		}
		return result;
	}

	public static class Builder {

		private CompoPredicate toBuild;

		Builder(String clientName, AbstractServerPredicate primaryPredicate) {
			toBuild = new CompoPredicate();
			toBuild.delegate = primaryPredicate;
			toBuild.clientName = clientName;
		}

		Builder(String clientName, AbstractServerPredicate... primaryPredicates) {
			toBuild = new CompoPredicate();
			toBuild.clientName = clientName;
			Predicate<PredicateKey> chain = Predicates.<PredicateKey>and(primaryPredicates);
			toBuild.delegate = AbstractServerPredicate.ofKeyPredicate(chain);
		}

		public Builder addFallbackPredicate(AbstractServerPredicate fallback) {
			toBuild.fallbacks.add(fallback);
			return this;
		}

		public Builder setFallbackThresholdAsMinimalFilteredNumberOfServers(int number) {
			toBuild.minimalFilteredServers = number;
			return this;
		}

		public Builder setFallbackThresholdAsMinimalFilteredPercentage(float percent) {
			toBuild.minimalFilteredPercentage = percent;
			return this;
		}

		public CompoPredicate build() {
			return toBuild;
		}
	}
}
