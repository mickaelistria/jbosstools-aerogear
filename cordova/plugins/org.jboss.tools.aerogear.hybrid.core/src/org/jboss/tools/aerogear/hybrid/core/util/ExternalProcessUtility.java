/*******************************************************************************
 * Copyright (c) 2013 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 *  Contributors:
 *       Red Hat, Inc. - initial API and implementation
 *******************************************************************************/
package org.jboss.tools.aerogear.hybrid.core.util;

import java.io.File;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.IStreamListener;
import org.eclipse.debug.core.Launch;
import org.eclipse.debug.core.model.IProcess;
import org.jboss.tools.aerogear.hybrid.core.HybridCore;
/**
 * Utilities for calling and processing the output from external executables.
 * 
 * @author Gorkem Ercan
 *
 */
public class ExternalProcessUtility {

	
	public void execAsync ( String commandLine, File workingDirectory, 
			IStreamListener outStreamListener, 
			IStreamListener errorStreamListener, String[] envp) throws CoreException{
		
		HybridCore.trace("Async Execute command line: "+commandLine);
		String[] cmd = DebugPlugin.parseArguments(commandLine);
		Process process =DebugPlugin.exec(cmd, workingDirectory, envp);
		
		
		Launch launch = new Launch(null, "run", null);
		IProcess prcs = DebugPlugin.newProcess(launch, process, "Cordova Plugin:  "+ cmd[0]);
		DebugPlugin.getDefault().getLaunchManager().addLaunch(launch);
		
		if( outStreamListener != null ){
			prcs.getStreamsProxy().getOutputStreamMonitor().addListener(outStreamListener);
		}
		if( errorStreamListener != null ){
			prcs.getStreamsProxy().getErrorStreamMonitor().addListener(errorStreamListener);
		}
	}
	
	public void execSync ( String commandLine, File workingDirectory, 
			IStreamListener outStreamListener, 
			IStreamListener errorStreamListener, IProgressMonitor monitor, String[] envp, ILaunchConfiguration launchConfiguration) throws CoreException{
		
		HybridCore.trace("Sync Execute command line: "+commandLine);
		String[] cmd = DebugPlugin.parseArguments(commandLine);
		Process process =DebugPlugin.exec(cmd, workingDirectory, envp);
		
		
		Launch launch = new Launch(launchConfiguration, "run", null);
		IProcess prcs = DebugPlugin.newProcess(launch, process, "Cordova Plugin:  "+ cmd[0]);
		if(launchConfiguration != null){
			DebugPlugin.getDefault().getLaunchManager().addLaunch(launch);
		}
		
		if( outStreamListener != null ){
			prcs.getStreamsProxy().getOutputStreamMonitor().addListener(outStreamListener);
		}
		if( errorStreamListener != null ){
			prcs.getStreamsProxy().getErrorStreamMonitor().addListener(errorStreamListener);
		}
		
		while (!prcs.isTerminated()) {
			try {
				if (monitor.isCanceled()) {
					prcs.terminate();
					break;
				}
				Thread.sleep(50);
			} catch (InterruptedException e) {
			}
		}
	}	
	
	
	
}
