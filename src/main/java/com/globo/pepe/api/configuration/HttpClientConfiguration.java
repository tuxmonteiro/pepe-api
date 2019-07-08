/*
 * Copyright (c) 2019 - Globo.com - ATeam
 * All rights reserved.
 *
 * This source is subject to the Apache License, Version 2.0.
 * Please see the LICENSE file for more information.
 *
 * Authors: See AUTHORS file
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.globo.pepe.api.configuration;

import com.globo.pepe.common.services.JsonLoggerService;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.SslProvider;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import java.util.Map;
import javax.net.ssl.SSLException;
import org.asynchttpclient.AsyncHttpClient;
import org.asynchttpclient.Dsl;
import org.asynchttpclient.Response;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.Assert;

@Configuration
public class HttpClientConfiguration {

    private final JsonLoggerService loggerService;

    public HttpClientConfiguration(JsonLoggerService loggerService) {
        this.loggerService = loggerService;
    }

    @Bean
    public HttpClient asyncHttpClient() {
        return new HttpClient();
    }

    public class HttpClient {

        private AsyncHttpClient asyncHttpClient = null;

        public HttpClient() {
            try {
                this.asyncHttpClient = Dsl.asyncHttpClient(Dsl.config()
                    .setConnectionTtl(10000)
                    .setPooledConnectionIdleTimeout(5000)
                    .setMaxConnections(10)
                    .setSslContext(
                        SslContextBuilder.forClient().sslProvider(SslProvider.JDK).trustManager(InsecureTrustManagerFactory.INSTANCE).build())
                    .build());
            } catch (SSLException e) {
                loggerService.newLogger(getClass()).message(e.getMessage()).sendError(e);
            }
        }

        public Response get(String url, Map<CharSequence, ? extends Iterable<?>> headers) throws Exception {
            Assert.notNull(asyncHttpClient, "AsyncHttpClient initialization problem");
            return asyncHttpClient.prepareGet(url)
                .setHeaders(headers)
                .execute()
                .get();
        }

        public Response post(
            String url, String body, Map<CharSequence, ? extends Iterable<?>> headers) throws Exception {
            Assert.notNull(asyncHttpClient, "AsyncHttpClient initialization problem");
            return asyncHttpClient.preparePost(url)
                .setHeaders(headers)
                .setBody(body)
                .execute()
                .get();
        }
    }

}
