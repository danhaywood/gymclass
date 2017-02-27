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
package domainapp.modules.employees.specglue;

import java.util.List;
import java.util.UUID;

import org.apache.isis.core.specsupport.specs.CukeGlueAbstract;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.When;
import domainapp.modules.employees.dom.impl.GymInstructor;
import domainapp.modules.employees.dom.impl.GymInstructorMenu;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class GymInstructorMenuGlue extends CukeGlueAbstract {

    @Given("^there are.* (\\d+) gym instructors$")
    public void there_are_N_gym_instructors(int n) throws Throwable {
        try {
            final List<GymInstructor> list = employeeMenu().listAll();
            assertThat(list.size(), is(n));
            putVar("java.util.List", "gymInstructors", list);
        } finally {
            assertMocksSatisfied();
        }
    }
    
    @When("^.*create a .*gym instructor$")
    public void create_a_gym_instructor() throws Throwable {
        employeeMenu().create(UUID.randomUUID().toString());
    }

    private GymInstructorMenu employeeMenu() {
        return service(GymInstructorMenu.class);
    }

}
