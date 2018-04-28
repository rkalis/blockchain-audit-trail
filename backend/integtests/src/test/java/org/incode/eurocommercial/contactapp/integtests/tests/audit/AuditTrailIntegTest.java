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

import org.incode.eurocommercial.contactapp.dom.audit.contracts.generated.AuditTrail;
import org.incode.eurocommercial.contactapp.fixture.contracts.GanacheAccounts;
import org.incode.eurocommercial.contactapp.integtests.tests.ContactAppIntegTest;

import static org.assertj.core.api.Assertions.assertThat;

public class AuditTrailIntegTest extends ContactAppIntegTest {
    Web3j web3j = Web3j.build(new HttpService("http://localhost:8545"));
    final Credentials TEST_CREDENTIALS = GanacheAccounts.TEST_ACCOUNT_CREDENTIALS.get(0);
    AuditTrail auditTrail;

    @Before
    public void setUp() throws Exception {
        auditTrail = AuditTrail.deploy(
                web3j,
                TEST_CREDENTIALS,
                new BigInteger("240000"),
                new BigInteger("2400000")
        ).send();
    }

    @After
    public void tearDown() throws Exception {
        auditTrail.kill().send();
    }

    public static class DeployContract extends AuditTrailIntegTest {
        @Test
        public void can_deploy_contract() throws Exception {
            assertThat(auditTrail.isValid());
        }
    }

    public static class Audit extends AuditTrailIntegTest {
        @Test
        public void can_add_to_audit_trail() throws Exception {
            // given
            String valueToAudit = "Hello World!";

            // when
            auditTrail.audit(valueToAudit).send();

            // then
            assertThat(auditTrail.auditEntries(BigInteger.ZERO).send()).isEqualTo(valueToAudit);
//            assertThat(auditTrail.isValid());
        }
    }
}