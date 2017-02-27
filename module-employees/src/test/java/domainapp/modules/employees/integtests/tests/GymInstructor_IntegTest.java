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
package domainapp.modules.employees.integtests.tests;

import java.sql.Timestamp;

import javax.inject.Inject;

import org.junit.Before;
import org.junit.Test;

import org.apache.isis.applib.fixturescripts.FixtureScripts;
import org.apache.isis.applib.services.title.TitleService;
import org.apache.isis.applib.services.wrapper.DisabledException;
import org.apache.isis.applib.services.wrapper.InvalidException;
import org.apache.isis.applib.services.xactn.TransactionService;
import org.apache.isis.core.metamodel.services.jdosupport.Persistable_datanucleusIdLong;
import org.apache.isis.core.metamodel.services.jdosupport.Persistable_datanucleusVersionTimestamp;

import domainapp.modules.employees.dom.impl.GymInstructor;
import domainapp.modules.employees.dom.impl.GymInstructorMenu;
import domainapp.modules.employees.fixture.scenario.CreateGymInstructors;
import domainapp.modules.employees.fixture.scenario.GymInstructorData;
import domainapp.modules.employees.fixture.teardown.EmployeesModuleTearDown;
import domainapp.modules.employees.integtests.EmployeesModuleIntegTestAbstract;
import static org.assertj.core.api.Assertions.assertThat;

public class GymInstructor_IntegTest extends EmployeesModuleIntegTestAbstract {

    @Inject
    FixtureScripts fixtureScripts;
    @Inject
    GymInstructorMenu gymInstructorMenu;
    @Inject
    TransactionService transactionService;

    GymInstructor gymInstructor;

    @Before
    public void setUp() throws Exception {
        // given
        fixtureScripts.runFixtureScript(new EmployeesModuleTearDown(), null);
        CreateGymInstructors fs = new CreateGymInstructors().setNumber(1);
        fixtureScripts.runFixtureScript(fs, null);
        transactionService.nextTransaction();

        gymInstructor = GymInstructorData.FOO.findWith(wrap(gymInstructorMenu));

        assertThat(gymInstructor).isNotNull();
    }

    public static class Name extends GymInstructor_IntegTest {

        @Test
        public void accessible() throws Exception {
            // when
            final String name = wrap(gymInstructor).getName();

            // then
            assertThat(name).isEqualTo(gymInstructor.getName());
        }

        @Test
        public void not_editable() throws Exception {
            // expect
            expectedExceptions.expect(DisabledException.class);

            // when
            wrap(gymInstructor).setName("new name");
        }

    }

    public static class UpdateName extends GymInstructor_IntegTest {

        @Test
        public void can_be_updated_directly() throws Exception {

            // when
            wrap(gymInstructor).updateName("new name");
            transactionService.nextTransaction();

            // then
            assertThat(wrap(gymInstructor).getName()).isEqualTo("new name");
        }

        @Test
        public void failsValidation() throws Exception {

            // expect
            expectedExceptions.expect(InvalidException.class);
            expectedExceptions.expectMessage("Exclamation mark is not allowed");

            // when
            wrap(gymInstructor).updateName("new name!");
        }
    }


    public static class Title extends GymInstructor_IntegTest {

        @Inject
        TitleService titleService;

        @Test
        public void interpolatesName() throws Exception {

            // given
            final String name = wrap(gymInstructor).getName();

            // when
            final String title = titleService.titleOf(gymInstructor);

            // then
            assertThat(title).isEqualTo("Object: " + name);
        }
    }

    public static class DataNucleusId extends GymInstructor_IntegTest {

        @Test
        public void should_be_populated() throws Exception {
            // when
            final Long id = mixin(Persistable_datanucleusIdLong.class, gymInstructor).exec();

            // then
            assertThat(id).isGreaterThanOrEqualTo(0);
        }
    }

    public static class DataNucleusVersionTimestamp extends GymInstructor_IntegTest {

        @Test
        public void should_be_populated() throws Exception {
            // when
            final Timestamp timestamp = mixin(Persistable_datanucleusVersionTimestamp.class, gymInstructor).exec();
            // then
            assertThat(timestamp).isNotNull();
        }
    }


}