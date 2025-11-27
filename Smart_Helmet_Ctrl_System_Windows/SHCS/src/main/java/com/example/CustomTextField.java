package com.example;

import javax.swing.*;
import javax.swing.text.*;

public class CustomTextField extends PlainDocument {
	private int maxLength;

	public CustomTextField(int maxLength) {
		this.maxLength = maxLength;
	}

	@Override
	public void insertString(int offset, String str, AttributeSet attr) throws BadLocationException {
		if (str == null) {
			return;
		}

		if ((getLength() + str.length()) <= maxLength) {
			super.insertString(offset, str, attr);
		}
	}
}
