/*
 * Copyright (C) 2017. Uber Technologies
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.uber.rib.core;

import androidx.annotation.IntRange;
import androidx.annotation.Nullable;
import org.checkerframework.checker.guieffect.qual.PolyUIEffect;
import org.checkerframework.checker.guieffect.qual.PolyUIType;
import org.checkerframework.checker.guieffect.qual.UIEffect;

/**
 * Simple utility for switching a child router based on a state.
 *
 * @param <StateT> type of state to switch on.
 */
public interface RouterNavigator<StateT extends RouterNavigatorState> {

  /** Determine how pushes will affect the stack */
  enum Flag {
    /** Push a new state to the top of the stack. */
    DEFAULT,

    /** Push a state that will not be retained when the next state is pushed. */
    TRANSIENT,

    /**
     * Start looking at the stack from the top until we see the state that is being pushed. If it is
     * found, remove all states that we traversed and transition the app to the found state. If the
     * state is not found in the stack, create a new instance and transition to it pushing it on top
     * of the stack.
     */
    CLEAR_TOP,

    /**
     * First create a new instance of the state and push it to the top of the stack. Then traverse
     * down the stack and and delete any instances of the state being pushed.
     */
    SINGLE_TOP,

    /**
     * Search through the stack for the state and if found, move the state to the top but don’t
     * change the stack otherwise. If the state doesn’t exist in the stack, create a new instance
     * and push to the top.
     */
    REORDER_TO_TOP,

    /** Clears the previous stack (no back stack) and pushes the state on to the top of the stack */
    NEW_TASK
  }

  /** Pop the current state and rewind to the previous state (if there is a previous state). */
  void popState();

  /**
   * Switch to a new state - this will switch out children if the state is not the current active
   * state already.
   *
   * <p>NOTE: This will retain the Riblet in memory until it is popped or detached by a push with
   * certain flags.
   *
   * @param newState to switch to.
   * @param attachTransition method to attach child router.
   * @param detachTransition method to clean up child router when removed.
   * @param <R> router type to detach.
   */
  <R extends Router> void pushState(
      StateT newState,
      AttachTransition<R, StateT> attachTransition,
      @Nullable DetachTransition<R, StateT> detachTransition);

  /**
   * Switch to a new state - this will switch out children if the state is not the current active
   * state already. The transition will be controlled by the {@link StackRouterNavigator.Flag}
   * provided.
   *
   * <p>NOTE: This will retain the Riblet in memory until it is popped or detached by a push with
   * certain flags.
   *
   * @param newState to switch to.
   * @param attachTransition method to attach child router.
   * @param detachTransition method to clean up child router when removed.
   * @param <R> router type to detach.
   */
  <R extends Router> void pushState(
      StateT newState,
      StackRouterNavigator.Flag flag,
      AttachTransition<R, StateT> attachTransition,
      @Nullable DetachTransition<R, StateT> detachTransition);

  /**
   * Switch to a new state - this will switch out children if the state is not the current active
   * state already.
   *
   * <p>NOTE: This will retain the Riblet in memory until it is popped. To push transient, riblets,
   * use {@link RouterNavigator#pushTransientState(RouterNavigatorState, AttachTransition,
   * DetachTransition)}
   *
   * <p>Deprecated: Use pushState(newState, attachTransition, detachTransition)
   *
   * @param newState to switch to.
   * @param attachTransition method to attach child router.
   * @param detachTransition method to clean up child router when removed.
   * @param <R> router type to detach.
   */
  @Deprecated
  <R extends Router> void pushRetainedState(
      final StateT newState,
      final AttachTransition<R, StateT> attachTransition,
      @Nullable final DetachTransition<R, StateT> detachTransition);

  /**
   * Switch to a new state - this will switch out children if the state is not the current active
   * state already.
   *
   * <p>NOTE: This will retain the Riblet in memory until it is popped. To push transient, riblets,
   * use {@link RouterNavigator#pushTransientState(RouterNavigatorState, AttachTransition,
   * DetachTransition)}
   *
   * <p>Deprecated: Use pushState(newState, attachTransition, null)
   *
   * @param newState to switch to.
   * @param attachTransition method to attach child router.
   * @param <R> {@link Router} type.
   */
  @Deprecated
  <R extends Router> void pushRetainedState(
      StateT newState, AttachTransition<R, StateT> attachTransition);

  /**
   * Switch to a new transient state - this will switch out children if the state is not the current
   * active state already.
   *
   * <p>NOTE: Transient states do not live in the back navigation stack.
   *
   * <p>Deprecated: Use pushState(newState, Flag.TRANSIENT, attachTransition, detachTransition)
   *
   * @param newState to switch to.
   * @param attachTransition method to attach child router.
   * @param detachTransition method to clean up child router when removed.
   * @param <R> router type to detach.
   */
  @Deprecated
  <R extends Router> void pushTransientState(
      final StateT newState,
      final AttachTransition<R, StateT> attachTransition,
      @Nullable final DetachTransition<R, StateT> detachTransition);

  /**
   * Switch to a new transient state - this will switch out children if the state is not the current
   * active state already.
   *
   * <p>NOTE: Transient states do not live in the back navigation stack.
   *
   * <p>Deprecated: Use pushState(newState, Flag.TRANSIENT, attachTransition, null)
   *
   * @param newState to switch to.
   * @param attachTransition method to attach child router.
   * @param <R> {@link Router} type.
   */
  @Deprecated
  <R extends Router> void pushTransientState(
      StateT newState, AttachTransition<R, StateT> attachTransition);

  /**
   * Peek the top {@link Router} on the stack.
   *
   * @return the top {@link Router} on the stack.
   */
  @Nullable
  Router peekRouter();

  /**
   * Peek the top {@link StateT} on the stack.
   *
   * @return the top {@link StateT} on the stack.
   */
  @Nullable
  StateT peekState();

  /**
   * Gets the size of the navigation stack.
   *
   * @return Size of the navigation stack.
   */
  @IntRange(from = 0)
  int size();

  /**
   * Must be called when host interactor is going to detach. This will pop the current active router
   * and clear the entire stack.
   */
  void hostWillDetach();

  /**
   * Allows consumers to write custom attachment logic when switching states.
   *
   * @param <StateT> state type.
   */
  interface AttachTransition<RouterT extends Router, StateT extends RouterNavigatorState> {

    /**
     * Constructs a new {@link RouterT} instance. This will only be called once.
     *
     * @return the newly attached child router.
     */
    RouterT buildRouter();

    /**
     * Prepares the router for a state transition. {@link StackRouterNavigator} will handling
     * attaching the router, but consumers of this should handle adding any views.
     *
     * @param router {@link RouterT} that is being attached.
     * @param previousState state the navigator is transition from (if any).
     * @param newState state the navigator is transitioning to.
     */
    @UIEffect
    void willAttachToHost(
        final RouterT router, @Nullable StateT previousState, StateT newState, boolean isPush);
  }

  /**
   * Allows consumers to write custom detachment logic when the state is changing. This allows for
   * custom state prior to and immediately post detach.
   *
   * @param <RouterT> {@link RouterT}
   * @param <StateT> {@link StateT}
   */
  abstract class DetachCallback<RouterT extends Router, StateT extends RouterNavigatorState>
      implements DetachTransition<RouterT, StateT> {
    @Override
    public void willDetachFromHost(
        RouterT router, StateT previousState, @Nullable StateT newState, boolean isPush) {}

    /**
     * Notifies the consumer that the {@link StackRouterNavigator} has detached the supplied {@link
     * Router}. Consumers can complete any post detachment behavior here.
     *
     * @param router {@link Router}
     * @param newState {@link StateT}
     */
    public void onPostDetachFromHost(RouterT router, @Nullable StateT newState, boolean isPush) {}
  }

  /**
   * Allows consumers to write custom detachment logic wen switching states.
   *
   * @param <RouterT> router type to detach.
   * @param <StateT> state type.
   */
  @PolyUIType
  interface DetachTransition<RouterT extends Router, StateT extends RouterNavigatorState> {

    /**
     * Notifies consumer that {@link StackRouterNavigator} is going to detach this router. Consumers
     * should remove any views and perform any required cleanup.
     *
     * @param router being removed.
     * @param previousState state the navigator is transitioning out of.
     * @param newState state the navigator is transition in to (if any).
     */
    @PolyUIEffect
    void willDetachFromHost(
        RouterT router, StateT previousState, @Nullable StateT newState, boolean isPush);
  }

  /** Internal class for keeping track of a navigation stack. */
  final class RouterAndState<StateT extends RouterNavigatorState> {

    private final Router router;
    private final StateT state;
    private final AttachTransition attachTransition;
    @Nullable private final DetachCallback detachCallback;

    @SuppressWarnings("unchecked")
    RouterAndState(
        Router router,
        StateT state,
        AttachTransition attachTransition,
        @Nullable DetachTransition detachTransition) {
      this.router = router;
      this.state = state;
      this.attachTransition = attachTransition;

      if (detachTransition != null) {
        if (detachTransition instanceof DetachCallback) {
          this.detachCallback = (DetachCallback) detachTransition;
        } else {
          this.detachCallback = new DetachCallbackWrapper<>(detachTransition);
        }
      } else {
        this.detachCallback = null;
      }
    }

    /**
     * Gets the {@link Router} associated with this state.
     *
     * @return {@link Router}
     */
    public Router getRouter() {
      return router;
    }

    /**
     * Gets the state.
     *
     * @return {@link StateT}
     */
    public StateT getState() {
      return state;
    }

    /**
     * Gets the {@link AttachTransition} associated with this state.
     *
     * @return {@link AttachTransition}
     */
    AttachTransition getAttachTransition() {
      return attachTransition;
    }

    /**
     * Gets the {@link DetachCallback} associated with this state.
     *
     * @return {@link DetachCallback}
     */
    @Nullable
    DetachCallback getDetachCallback() {
      return detachCallback;
    }
  }

  /**
   * Wrapper class to wrap {@link DetachTransition} calls into the new {@link DetachCallback}
   * format.
   *
   * @param <RouterT> {@link RouterT}
   * @param <StateT> {@link StateT}
   */
  final class DetachCallbackWrapper<RouterT extends Router, StateT extends RouterNavigatorState>
      extends DetachCallback<RouterT, StateT> {
    private final DetachTransition<RouterT, StateT> transitionCallback;

    DetachCallbackWrapper(final DetachTransition<RouterT, StateT> transitionCallback) {
      this.transitionCallback = transitionCallback;
    }

    @Override
    public void willDetachFromHost(
        RouterT router, StateT previousState, @Nullable StateT newState, boolean isPush) {
      transitionCallback.willDetachFromHost(router, previousState, newState, isPush);
    }
  }
}
