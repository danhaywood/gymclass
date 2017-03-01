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

import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.VersionStrategy;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.Editing;
import org.apache.isis.applib.annotation.Parameter;
import org.apache.isis.applib.annotation.ParameterLayout;
import org.apache.isis.applib.annotation.Property;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.annotation.Title;
import org.apache.isis.applib.services.i18n.TranslatableString;
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
                        + "WHERE firstName.indexOf(:firstName) >= 0 ")
})
@javax.jdo.annotations.Unique(name="Person_firstName_UNQ", members = {"firstName"})
@DomainObject() // objectType inferred from @PersistenceCapable#schema
public class Person implements Comparable<Person> {

    public Person(final String firstName) {
        setFirstName(firstName);
    }

    @javax.jdo.annotations.Column(allowsNull = "false", length = 40)
    @Property() // editing disabled by default, see isis.properties
    @Getter @Setter
    @Title(prepend = "Object: ")
    private String firstName;

    @javax.jdo.annotations.Column(allowsNull = "true", length = 4000)
    @Property(editing = Editing.ENABLED)
    @Getter @Setter
    private String notes;


    //region > updateName (action)
    @Action(semantics = SemanticsOf.IDEMPOTENT)
    public Person updateName(
            @Parameter(maxLength = 40)
            @ParameterLayout(named = "First name")
            final String firstName) {
        setFirstName(firstName);
        return this;
    }

    public String default0UpdateName() {
        return getFirstName();
    }

    public TranslatableString validate0UpdateName(final String firstName) {
        return firstName != null && firstName.contains("!") ? TranslatableString.tr("Exclamation mark is not allowed") : null;
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
        return ObjectContracts.toString(this, "firstName");
    }

    @Override
    public int compareTo(final Person other) {
        return ObjectContracts.compare(this, other, "firstName");
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

}