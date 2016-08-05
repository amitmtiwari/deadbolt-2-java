/*
 * Copyright 2013 Steve Chaloner
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package be.objectify.deadbolt.java.test.controllers.path;

import be.objectify.deadbolt.java.test.controllers.SubjectPresentTest;

/**
 * @author Steve Chaloner (steve@objectify.be)
 */
public class RoutePathSubjectPresentTest extends SubjectPresentTest
{
    @Override
    public String pathComponent()
    {
        return "rp";
    }
}
