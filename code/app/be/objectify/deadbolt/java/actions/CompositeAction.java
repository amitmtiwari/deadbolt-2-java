/*
 * Copyright 2010-2017 Steve Chaloner
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
package be.objectify.deadbolt.java.actions;

import java.util.Optional;
import java.util.concurrent.CompletionStage;
import javax.inject.Inject;
import be.objectify.deadbolt.java.ConstraintLogic;
import be.objectify.deadbolt.java.ConstraintPoint;
import be.objectify.deadbolt.java.DeadboltHandler;
import be.objectify.deadbolt.java.ExecutionContextProvider;
import be.objectify.deadbolt.java.cache.CompositeCache;
import be.objectify.deadbolt.java.cache.HandlerCache;
import play.Configuration;
import play.mvc.Http;
import play.mvc.Result;
import scala.concurrent.ExecutionContextExecutor;

/**
 * @author Steve Chaloner (steve@objectify.be)
 */
public class CompositeAction extends AbstractRestrictiveAction<Composite>
{
    private final CompositeCache compositeCache;

    @Inject
    public CompositeAction(final HandlerCache handlerCache,
                           final Configuration config,
                           final ExecutionContextProvider ecProvider,
                           final CompositeCache compositeCache,
                           final ConstraintLogic constraintLogic)
    {
        super(handlerCache,
              config,
              ecProvider,
              constraintLogic);
        this.compositeCache = compositeCache;
    }

    @Override
    public CompletionStage<Result> applyRestriction(final Http.Context ctx,
                                                    final DeadboltHandler handler)
    {
        final ExecutionContextExecutor executor = executor();
        return compositeCache.apply(getValue())
                             .map(constraint ->
                                  {
                                      final boolean preferGlobalMeta = configuration.preferGlobalMeta();
                                      return constraint.test(ctx,
                                                             handler,
                                                             executor,
                                                             Optional.ofNullable(getMeta()),
                                                             (globalMd, localMd) -> preferGlobalMeta ? globalMd.isPresent() ? globalMd : localMd
                                                                                                     : localMd.isPresent() ? localMd : globalMd)
                                                       .thenComposeAsync(allowed -> allowed ? authorizeAndExecute(ctx,
                                                                                                                  handler)
                                                                                            : unauthorizeAndFail(ctx,
                                                                                                                 handler,
                                                                                                                 Optional.ofNullable(configuration.content())),
                                                                         executor);
                                  })
                             .orElseGet(() ->
                                        {
                                            markActionAsUnauthorised(ctx);
                                            return onAuthFailure(handler,
                                                                 Optional.ofNullable(configuration.content()),
                                                                 ctx);
                                        });
    }

    public String getMeta()
    {
        return configuration.meta();
    }

    public String getValue()
    {
        return configuration.value();
    }

    private CompletionStage<Result> authorizeAndExecute(final Http.Context context,
                                                        final DeadboltHandler handler) 
    {
        handler.onAuthSuccess(context,
                              "composite",
                              ConstraintPoint.CONTROLLER);
        return authorizeAndExecute(context);
    }

    @Override
    public String getHandlerKey()
    {
        return configuration.handlerKey();
    }
}
