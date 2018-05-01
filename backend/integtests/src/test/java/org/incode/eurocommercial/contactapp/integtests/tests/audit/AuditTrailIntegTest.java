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

import javax.inject.Inject;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import org.incode.eurocommercial.contactapp.dom.audit.AuditEntry;
import org.incode.eurocommercial.contactapp.dom.audit.Web3Service;
import org.incode.eurocommercial.contactapp.dom.audit.contracts.generated.AuditTrail;
import org.incode.eurocommercial.contactapp.integtests.tests.ContactAppIntegTest;

import static org.assertj.core.api.Assertions.assertThat;

public class AuditTrailIntegTest extends ContactAppIntegTest {
    @Inject Web3Service web3Service;
    AuditTrail auditTrail;

    @Before
    public void setUp() throws Exception {
        auditTrail = AuditTrail.deploy(
                web3Service.getWeb3j(),
                web3Service.getCredentials(),
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
            String preValue = "Hello World!";
            String postValue = "Hi Y'all!";

            // when
            auditTrail.audit(preValue, postValue).send();

            // then
            AuditEntry auditEntry = AuditEntry.fromTuple(auditTrail.auditEntries(BigInteger.ZERO).send());
            assertThat(auditEntry.getPreValue()).isEqualTo(preValue);
            assertThat(auditEntry.getPostValue()).isEqualTo(postValue);
        }
    }
}