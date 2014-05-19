/*******************************************************************************
 * Copyright 2012 Charles University in Prague
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package cz.cuni.mff.ms.siptak.adeecolib.cloud;

import java.util.Random;

import cz.cuni.mff.d3s.deeco.annotations.DEECoComponent;
import cz.cuni.mff.d3s.deeco.annotations.DEECoIn;
import cz.cuni.mff.d3s.deeco.annotations.DEECoInOut;
import cz.cuni.mff.d3s.deeco.annotations.DEECoInitialize;
import cz.cuni.mff.d3s.deeco.annotations.DEECoPeriodicScheduling;
import cz.cuni.mff.d3s.deeco.annotations.DEECoProcess;
import cz.cuni.mff.d3s.deeco.knowledge.OutWrapper;
import cz.cuni.mff.d3s.deeco.knowledge.ComponentKnowledge;
import cz.cuni.mff.ms.siptak.adeecolib.service.AppMessenger;
import cz.cuni.mff.ms.siptak.adeecolib.service.AppMessenger.AppLogger;

@DEECoComponent
public class NodeB extends ComponentKnowledge {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6399971742738465623L;
	public Float loadRatio;
	public Float maxLoadRatio;
	public Integer networkId;
	public String targetNode;
	public String id;

	@DEECoInitialize
	public static ComponentKnowledge getInitialKnowledge() {
		NodeB k = new NodeB();
		k.id = "Node B";
		k.loadRatio = 0.0f;
		k.maxLoadRatio = 0.7f;
		k.networkId = 1;
		k.targetNode = null;
		return k;
	}

	@DEECoProcess
	@DEECoPeriodicScheduling(10000)
	public static void process(@DEECoIn("id") String id, @DEECoInOut("loadRatio") OutWrapper<Float> loadRatio) {
		Float old = loadRatio.item;
		loadRatio.item = new Random().nextFloat();
		String text = id+" load from "+Math.round(old * 100)+ "% to "+Math.round(loadRatio.item * 100)+"%";
		//System.out.println(text);
		AppLogger logger = AppMessenger.getInstance().getLogger(id);
		logger.addLog(text);
	}
}
