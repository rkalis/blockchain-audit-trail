/**
 * Copyright 2018 Rosco Kalis
 * <p>
 * Licensed under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.incode.eurocommercial.contactapp.dom.audit;

import java.util.List;
import java.util.stream.Collectors;

import com.google.common.collect.Lists;

import org.web3j.crypto.Credentials;

public class GanacheAccounts {
    /* Accounts for default Ganache mnemonic 'candy maple cake sugar pudding cream honey rich smooth crumble sweet treat' */
    public static final List<String> TEST_ACCOUNT_PRIVATE_KEYS = Lists.newArrayList(
            "c87509a1c067bbde78beb793e6fa76530b6382a4c0241e5e4a9ec0a0f44dc0d3",
            "ae6ae8e5ccbfb04590405997ee2d52d2b330726137b875053c36d94e974d162f",
            "0dbbe8e4ae425a6d2687f1a7e3ba17bc98c673636790f1b8ad91193c05875ef1",
            "c88b703fb08cbea894b6aeff5a544fb92e78a18e19814cd85da83b71f772aa6c",
            "388c684f0ba1ef5017716adb5d21a053ea8e90277d0868337519f97bede61418",
            "659cbb0e2411a44db63778987b1e22153c086a95eb6b18bdf89de078917abc63",
            "82d052c865f5763aad42add438569276c00d3d88a2d062d36b2bae914d58b8c8",
            "aa3680d5d48a8283413f7a108367c7299ca73f553735860a87b08f39395618b7",
            "0f62d96d6675f32685bbdb8ac13cda7c23436f63efbb9d07700d8669ff12b7c4",
            "8d5366123cb560bb606379f90a0bfd4769eecc0557f1b362dcae9012b548b1e5"
    );
    public static final List<Credentials> TEST_ACCOUNT_CREDENTIALS = TEST_ACCOUNT_PRIVATE_KEYS.stream()
            .map(Credentials::create)
            .collect(Collectors.toList());
}
