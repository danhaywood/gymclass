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
package domainapp.modules.timetable.integtests.tests;

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

import domainapp.modules.timetable.dom.impl.GymClassDescription;
import domainapp.modules.timetable.dom.impl.GymClassDescriptionMenu;
import domainapp.modules.timetable.fixture.scenario.CreateGymClassDescriptions;
import domainapp.modules.timetable.fixture.scenario.GymClassDescriptionData;
import domainapp.modules.timetable.fixture.teardown.TimeTableModuleTearDown;
import domainapp.modules.timetable.integtests.TimeTableModuleIntegTestAbstract;
import static org.assertj.core.api.Assertions.assertThat;

public class TimeTableObject_IntegTest extends TimeTableModuleIntegTestAbstract {

    @Inject
    FixtureScripts fixtureScripts;
    @Inject
    GymClassDescriptionMenu gymClassDescriptionMenu;
    @Inject
    TransactionService transactionService;

    GymClassDescription gymClassDescription;

    @Before
    public void setUp() throws Exception {
        // given
        fixtureScripts.runFixtureScript(new TimeTableModuleTearDown(), null);
        CreateGymClassDescriptions fs = new CreateGymClassDescriptions().setNumber(1);
        fixtureScripts.runFixtureScript(fs, null);
        transactionService.nextTransaction();

        gymClassDescription = GymClassDescriptionData.FOO.findWith(wrap(gymClassDescriptionMenu));

        assertThat(gymClassDescription).isNotNull();
    }

    public static class Name extends TimeTableObject_IntegTest {

        @Test
        public void accessible() throws Exception {
            // when
            final String name = wrap(gymClassDescription).getName();

            // then
            assertThat(name).isEqualTo(gymClassDescription.getName());
        }

        @Test
        public void not_editable() throws Exception {
            // expect
            expectedExceptions.expect(DisabledException.class);

            // when
            wrap(gymClassDescription).setName("new name");
        }

    }

    public static class UpdateName extends TimeTableObject_IntegTest {

        @Test
        public void can_be_updated_directly() throws Exception {

            // when
            wrap(gymClassDescription).updateName("new name");
            transactionService.nextTransaction();

            // then
            assertThat(wrap(gymClassDescription).getName()).isEqualTo("new name");
        }

        @Test
        public void failsValidation() throws Exception {

            // expect
            expectedExceptions.expect(InvalidException.class);
            expectedExceptions.expectMessage("Exclamation mark is not allowed");

            // when
            wrap(gymClassDescription).updateName("new name!");
        }
    }


    public static class Title extends TimeTableObject_IntegTest {

        @Inject
        TitleService titleService;

        @Test
        public void interpolatesName() throws Exception {

            // given
            final String name = wrap(gymClassDescription).getName();

            // when
            final String title = titleService.titleOf(gymClassDescription);

            // then
            assertThat(title).isEqualTo("Object: " + name);
        }
    }

    public static class DataNucleusId extends TimeTableObject_IntegTest {

        @Test
        public void should_be_populated() throws Exception {
            // when
            final Long id = mixin(Persistable_datanucleusIdLong.class, gymClassDescription).exec();

            // then
            assertThat(id).isGreaterThanOrEqualTo(0);
        }
    }

    public static class DataNucleusVersionTimestamp extends TimeTableObject_IntegTest {

        @Test
        public void should_be_populated() throws Exception {
            // when
            final Timestamp timestamp = mixin(Persistable_datanucleusVersionTimestamp.class, gymClassDescription).exec();
            // then
            assertThat(timestamp).isNotNull();
        }
    }


}