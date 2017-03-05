/*
 *  Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
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

package domainapp.modules.persons.fixture.scenario;

import domainapp.modules.persons.dom.impl.Person;
import domainapp.modules.persons.dom.impl.PersonMenu;
import domainapp.modules.persons.dom.impl.PersonRepository;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum PersonData {

    FREDA_MCLINTOCK("Freda", "McLintock"),
    BARRY_BLACK("Barry", "Black"),
    SEBASTIAN_SMITH("Sebastian", "Smith"),
    FIONA_BAGGINS("Fiona", "Baggins"),
    HARRY_SLATER("Harry", "Slater");

    private final String firstName;
    private final String lastName;

    public Person createWith(final PersonMenu menu) {
        return menu.create(firstName, lastName);
    }

    public Person findWith(final PersonRepository personRepository) {
        return personRepository.findByFirstNameAndLastName(firstName, lastName);
    }
}
