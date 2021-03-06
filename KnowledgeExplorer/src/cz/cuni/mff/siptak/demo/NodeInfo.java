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
package cz.cuni.mff.siptak.demo;

import java.util.Random;
import java.util.UUID;

import cz.cuni.mff.d3s.deeco.annotations.*;
import cz.cuni.mff.d3s.deeco.knowledge.ComponentKnowledge;
import cz.cuni.mff.d3s.deeco.knowledge.OutWrapper;
import cz.cuni.mff.d3s.events.EventFactory;
import cz.cuni.mff.d3s.events.MessageEvent;

@DEECoComponent
public class NodeInfo extends ComponentKnowledge {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7846606551654688528L;
	public Float loadRatio;
	public Float maxLoadRatio;
	public Integer networkId;
	public String targetNode;
	
	public String id;
	
	@DEECoInitialize
	public static ComponentKnowledge getInitialKnowledge() {
		NodeInfo k = new NodeInfo();
		// create unique name for the component
		k.id = UUID.randomUUID().toString().substring(0,8)+" "+k.getClass().getSimpleName();
		k.loadRatio = 0.0f;
		k.maxLoadRatio = 0.5f;
		k.networkId = 1;
		k.targetNode = null;
		return k;
	}

	@DEECoProcess
	@DEECoPeriodicScheduling(3000)
	public static void process(@DEECoIn("id") String id,@DEECoInOut("loadRatio") OutWrapper<Float> loadRatio) {
		Float old = loadRatio.item;
		loadRatio.item = new Random().nextFloat();
		String text = "Load from "+Math.round(old * 100)+ "% to "+Math.round(loadRatio.item * 100)+"%";
		System.out.println(text);
		//EventBus.getDefault().post(new ChangedKnowledgeEvent("NodeA", text));
		//EventBus.getDefault().post(new ChangedKnowledgeEvent("NodeA", text));
		EventFactory.getEventBus().post(new MessageEvent(id, text));
	}
}
