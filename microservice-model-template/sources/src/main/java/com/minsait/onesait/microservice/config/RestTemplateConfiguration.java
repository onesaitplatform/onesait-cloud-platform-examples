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

package com.minsait.onesait.microservice.config;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import com.minsait.onesait.microservice.error.ModelErrorHandler;

@Configuration
public class RestTemplateConfiguration {
	
	@Value("${security.basic.avoidSsl}")
	private boolean avoidSslCertificate;
	
	@Bean
	public RestTemplate initializeRestTemplate() {
	  if (avoidSslCertificate) {
			HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {
	            public boolean verify(String hostname, SSLSession session) {
	                return true;
	            }
	        });
	  }
	  
	  final RestTemplate restTemplate = new RestTemplate(new HttpComponentsClientHttpRequestFactory());
	  restTemplate.setErrorHandler(new ModelErrorHandler());
	  
	  return restTemplate;
	  }

}
