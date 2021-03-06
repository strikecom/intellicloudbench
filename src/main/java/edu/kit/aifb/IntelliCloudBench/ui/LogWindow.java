/*
 * This file is part of IntelliCloudBench.
 *
 * Copyright (c) 2012, Jan Gerlinger <jan.gerlinger@gmx.de>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * * Redistributions of source code must retain the above copyright
 * notice, this list of conditions and the following disclaimer.
 * * Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 * * Neither the name of the Institute of Applied Informatics and Formal
 * Description Methods (AIFB) nor the names of its contributors may be used to
 * endorse or promote products derived from this software without specific prior
 * written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL <COPYRIGHT HOLDER> BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package edu.kit.aifb.IntelliCloudBench.ui;

import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

import edu.kit.aifb.libIntelliCloudBench.CloudBenchService;
import edu.kit.aifb.libIntelliCloudBench.logging.ILogListener;
import edu.kit.aifb.libIntelliCloudBench.model.InstanceState;
import edu.kit.aifb.libIntelliCloudBench.model.InstanceType;

public class LogWindow extends Window implements ILogListener {
	private static final long serialVersionUID = -5700883676747080306L;

	private TextField logField;

	private StringBuilder sb = new StringBuilder();

	private CloudBenchService service;

	public LogWindow(final InstanceState instanceState, CloudBenchService service) {
		this.service = service;

		InstanceType instanceType = instanceState.getInstanceType();

		setWidth("750px");
		setHeight("500px");
		setCaption("Log for " + instanceType.getProvider().getName() + ", " + instanceType.getRegion().getId() + ", "
		    + instanceType.getHardwareType().getId());
		center();

		VerticalLayout layout = new VerticalLayout();
		layout.setSizeFull();
		layout.setSpacing(true);
		layout.setMargin(true);
		setContent(layout);

		logField = new TextField();
		logField.setSizeFull();
		logField.setImmediate(true);
		layout.addComponent(logField);
		layout.setExpandRatio(logField, 1.0f);

		Button close = new Button("Close");
		close.setWidth("-1px");
		close.setHeight("-1px");
		close.setStyleName("big");
		close.addListener(new ClickListener() {
			private static final long serialVersionUID = 5356614540664446119L;

			@Override
			public void buttonClick(ClickEvent event) {
				instanceState.unregisterListener(LogWindow.this);
				close();
			}

		});
		layout.addComponent(close);
		layout.setComponentAlignment(close, Alignment.BOTTOM_RIGHT);

		instanceState.registerListener(this);
		sb.append(instanceState.getLog());
		String log = sb.toString();
		logField.setValue(log);
		logField.setCursorPosition(log.length() - 1);
		logField.setReadOnly(true);
	}

	@Override
	public void updateLog(String newLine) {
		sb.append("\n");
		sb.append(newLine);
		String log = sb.toString();

		logField.setReadOnly(false);
		logField.setValue(log);
		logField.setReadOnly(true);
		logField.setCursorPosition(log.length() - 1);

		service.getPusher().push();
	}

}
