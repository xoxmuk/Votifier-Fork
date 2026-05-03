package ru.xoxmuk.votifierfork.model;

import org.bukkit.event.*;

public class VotifierEvent extends Event {

	private static final HandlerList handlers = new HandlerList();

	private Vote vote;

	public VotifierEvent(final Vote vote) {
		this.vote = vote;
	}

	public Vote getVote() {
		return vote;
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}
}
