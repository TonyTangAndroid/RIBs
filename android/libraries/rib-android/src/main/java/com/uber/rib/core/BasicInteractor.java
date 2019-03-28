package com.uber.rib.core;

/**
 * {@link Interactor} that doesn't rely on field injection.
 *
 * @param <P> the type of {@link Presenter}.
 * @param <R> the type of {@link Router}.
 */
public abstract class BasicInteractor<P, R extends Router> extends Interactor<P, R> {

  @SuppressWarnings("HidingField")
  protected P presenter;

  protected BasicInteractor(P presenter) {
    super(presenter);
    this.presenter = presenter;
  }
}
