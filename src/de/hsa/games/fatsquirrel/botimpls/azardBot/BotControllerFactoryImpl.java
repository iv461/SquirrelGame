package de.hsa.games.fatsquirrel.botimpls.azardBot;

import com.ivj.squirrelgame.botapi.BotController;
import com.ivj.squirrelgame.botapi.BotControllerFactory;

public class BotControllerFactoryImpl implements BotControllerFactory {
	@Override
	public BotController createMasterBotController() {

		return new AzardBot(false);
	}

	@Override
	public BotController createMiniBotController() {
		return new AzardBot(true);
	}
}
