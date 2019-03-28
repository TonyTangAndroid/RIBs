package com.uber.rib.core;

import android.view.View;

/**
 * {@link ViewRouter} that does not require an {@link InteractorBaseComponent}.
 *
 * @param <I> type of interactor.
 */
public class BasicViewRouter<V extends View, I extends Interactor>
    extends ViewRouter<V, I, InteractorBaseComponent> {

  public BasicViewRouter(V view, I interactor) {
    super(view, interactor);
  }
}
