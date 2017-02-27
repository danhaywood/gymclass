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
package domainapp.modules.classes.specglue;

import java.util.List;
import java.util.UUID;

import org.apache.isis.core.specsupport.specs.CukeGlueAbstract;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.When;
import domainapp.modules.classes.dom.impl.ScheduledGymClass;
import domainapp.modules.classes.dom.impl.ScheduledGymClassMenu;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class ScheduledGymClassMenuGlue extends CukeGlueAbstract {

    @Given("^there are.* (\\d+) scheduled gym classes$")
    public void there_are_N_scheduled_gym_classes(int n) throws Throwable {
        try {
            final List<ScheduledGymClass> list = simpleObjectMenu().listAll();
            assertThat(list.size(), is(n));
            putVar("java.util.List", "scheduledGymClasses", list);
        } finally {
            assertMocksSatisfied();
        }
    }
    
    @When("^.*create a .*scheduled gym class$")
    public void create_a_scheduled_gym_class() throws Throwable {
        simpleObjectMenu().create(UUID.randomUUID().toString());
    }

    private ScheduledGymClassMenu simpleObjectMenu() {
        return service(ScheduledGymClassMenu.class);
    }

}
