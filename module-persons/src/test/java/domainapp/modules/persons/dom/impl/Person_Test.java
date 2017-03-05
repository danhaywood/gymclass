/**
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package domainapp.modules.persons.dom.impl;

import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class Person_Test {

    Person person;

    @Before
    public void setUp() throws Exception {
        person = new Person("Billy", "Mason");
    }

    public static class FirstName extends Person_Test {

        @Test
        public void happyCase() throws Exception {
            // given
            assertThat(person.getFirstName()).isEqualTo("Billy");
            assertThat(person.getLastName()).isEqualTo("Mason");

            // when
            String name = "William";
            person.setFirstName(name);

            // then
            assertThat(person.getFirstName()).isEqualTo(name);
        }
    }

}
