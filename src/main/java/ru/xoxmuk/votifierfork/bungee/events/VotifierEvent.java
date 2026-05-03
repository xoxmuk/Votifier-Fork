package ru.xoxmuk.votifierfork.bungee.events;

import ru.xoxmuk.votifierfork.model.Vote;

import net.md_5.bungee.api.plugin.Event;

public class VotifierEvent extends Event {

	private Vote vote;

	public VotifierEvent(final Vote vote) {
		this.vote = vote;
	}

	public Vote getVote() {
		return vote;
	}

}
