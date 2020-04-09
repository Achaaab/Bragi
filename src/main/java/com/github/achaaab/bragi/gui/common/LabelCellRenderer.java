package com.github.achaaab.bragi.gui.common;

import javax.swing.JList;
import javax.swing.plaf.basic.BasicComboBoxRenderer;
import java.awt.Component;
import java.util.function.Function;

public class LabelCellRenderer<E> extends BasicComboBoxRenderer {

	private Function<? super E, String> toString;

	/**
	 * @param toString
	 */
	public LabelCellRenderer(Function<? super E, String> toString) {
		this.toString = toString;
	}

	@Override
	public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {

		super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
		setText(toString.apply((E) value));
		return this;
	}
}
