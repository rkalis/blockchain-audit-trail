/*
 *  Copyright 2015-2016 Eurocommercial Properties NV
 *
 *  Licensed under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */
package org.incode.eurocommercial.contactapp.integtests.tests.audit;

import java.math.BigInteger;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.http.HttpService;

import org.incode.eurocommercial.contactapp.dom.audit.contracts.generated.Greeter;
import org.incode.eurocommercial.contactapp.fixture.contracts.GanacheAccounts;
import org.incode.eurocommercial.contactapp.integtests.tests.ContactAppIntegTest;

import static org.assertj.core.api.Assertions.assertThat;

public class GreeterIntegTest extends ContactAppIntegTest {
    Web3j web3j = Web3j.build(new HttpService("http://localhost:8545"));
    final Credentials TEST_CREDENTIALS = GanacheAccounts.TEST_ACCOUNT_CREDENTIALS.get(0);
    final String GREETING = "Hello World!";
    Greeter greeter;

    @Before
    public void setUp() throws Exception {
        greeter = Greeter.deploy(
                web3j,
                TEST_CREDENTIALS,
                new BigInteger("240000"),
                new BigInteger("2400000"),
                GREETING
        ).send();
    }

    @After
    public void tearDown() throws Exception {
        greeter.kill().send();
    }

    public static class DeployContract extends GreeterIntegTest {
        @Test
        public void can_deploy_contract() throws Exception {
            assertThat(greeter.greet().send()).isEqualTo(GREETING);
        }
    }

}