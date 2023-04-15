package com.github.achaaab.bragi.gui.common;

import javax.swing.JList;
import javax.swing.plaf.basic.BasicComboBoxRenderer;
import java.awt.Component;
import java.util.function.Function;

/**
 * Extends {@link BasicComboBoxRenderer} but add a toString function which allow to display a customized text.
 *
 * @param <E> type of the objects in the combo box
 * @author Jonathan Guéhenneux
 * @since 0.1.8
 */
public class LabelCellRenderer<E> extends BasicComboBoxRenderer {

	private final Function<? super E, String> textFunction;

	/**
	 * @param textFunction function which takes an object of the combo box and returns a string
	 * @since 0.2.0
	 */
	public LabelCellRenderer(Function<? super E, String> textFunction) {
		this.textFunction = textFunction;
	}

	@Override
	public Component getListCellRendererComponent(
			JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {

		super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
		setText(textFunction.apply((E) value));
		return this;
	}
}
