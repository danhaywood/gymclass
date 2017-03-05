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
package domainapp.modules.persons.dom.impl;

import java.util.Map;

import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.VersionStrategy;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.Editing;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Parameter;
import org.apache.isis.applib.annotation.ParameterLayout;
import org.apache.isis.applib.annotation.Property;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.annotation.Title;
import org.apache.isis.applib.services.exceprecog.ExceptionRecognizer2;
import org.apache.isis.applib.services.message.MessageService;
import org.apache.isis.applib.services.repository.RepositoryService;
import org.apache.isis.applib.services.title.TitleService;
import org.apache.isis.applib.util.ObjectContracts;

import lombok.Getter;
import lombok.Setter;

@javax.jdo.annotations.PersistenceCapable(
        identityType=IdentityType.DATASTORE,
        schema = "persons"
)
@javax.jdo.annotations.DatastoreIdentity(
        strategy=javax.jdo.annotations.IdGeneratorStrategy.IDENTITY,
        column="id")
@javax.jdo.annotations.Version(
        strategy= VersionStrategy.DATE_TIME,
        column="version")
@javax.jdo.annotations.Queries({
        @javax.jdo.annotations.Query(
                name = "findByName",
                value = "SELECT "
                        + "FROM domainapp.modules.persons.dom.impl.Person "
                        + "WHERE firstName.indexOf(:name) >= 0 "
                        + "   || lastName.indexOf(:name) >= 0 "),
        @javax.jdo.annotations.Query(
                name = "findByFirstNameAndLastName",
                value = "SELECT "
                        + "FROM domainapp.modules.persons.dom.impl.Person "
                        + "WHERE firstName == :firstName "
                        + "   && lastName == :lastName ")
})
@javax.jdo.annotations.Unique(name="Person_lastName_firstName_UNQ", members = {"lastName", "firstName"})
@DomainObject() // objectType inferred from @PersistenceCapable#schema
public class Person implements Comparable<Person> {

    public Person(final String firstName, final String lastName) {
        setFirstName(firstName);
        setLastName(lastName);
    }

    @javax.jdo.annotations.Column(allowsNull = "false", length = 40)
    @Property()
    @Getter @Setter
    @Title(sequence = "1")
    private String firstName;

    @javax.jdo.annotations.Column(allowsNull = "false", length = 40)
    @Property()
    @Getter @Setter
    @Title(sequence = "2", prepend = " ")
    private String lastName;

    @javax.jdo.annotations.Column(allowsNull = "true", length = 4000)
    @Property(editing = Editing.ENABLED)
    @Getter @Setter
    private String notes;


    //region > updateName (action)
    @Action(semantics = SemanticsOf.IDEMPOTENT)
    public Person updateName(
            @Parameter(maxLength = 40)
            @ParameterLayout(named = "First name")
            final String firstName,
            @Parameter(maxLength = 40)
            @ParameterLayout(named = "Last name")
            final String lastName) {
        setFirstName(firstName);
        setLastName(lastName);
        return this;
    }

    public String default0UpdateName() {
        return getFirstName();
    }

    public String default1UpdateName() {
        return getLastName();
    }
    //endregion

    //region > delete (action)
    @Action(semantics = SemanticsOf.NON_IDEMPOTENT_ARE_YOU_SURE)
    public void delete() {
        final String title = titleService.titleOf(this);
        messageService.informUser(String.format("'%s' deleted", title));
        repositoryService.remove(this);
    }
    //endregion


    //region > toString, compareTo
    @Override
    public String toString() {
        return ObjectContracts.toString(this, "lastName", "firstName");
    }

    @Override
    public int compareTo(final Person other) {
        return ObjectContracts.compare(this, other, "lastName", "firstName");
    }
    //endregion

    //region > injected services
    @javax.inject.Inject
    RepositoryService repositoryService;

    @javax.inject.Inject
    TitleService titleService;

    @javax.inject.Inject
    MessageService messageService;
    //endregion

    @DomainService(nature = NatureOfService.DOMAIN)
    public static class UniqueConstraintViolationRecognizer implements ExceptionRecognizer2 {

        @Override
        public void init(final Map<String, String> properties) {
        }

        @Override
        public void shutdown() {
        }

        @Override
        public String recognize(final Throwable ex) {
            final String message = ex.getMessage();
            if(message != null && message.contains("Person_lastName_firstName_UNQ")) {
                return "A Person with that (first and last) name already exists";
            }
            return null;
        }

        @Override
        public Recognition recognize2(final Throwable ex) {
            final String reason = recognize(ex);
            return reason != null
                    ? new Recognition(Category.CONSTRAINT_VIOLATION, reason)
                    : null;
        }
    }
}