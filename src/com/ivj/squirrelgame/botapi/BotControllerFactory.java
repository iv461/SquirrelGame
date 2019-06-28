package com.ivj.squirrelgame.botapi;

public interface BotControllerFactory {
	BotController createMasterBotController();

	BotController createMiniBotController();
}