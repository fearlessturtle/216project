package com.sportsmanager.sports.football;

import com.sportsmanager.core.AbstractMatch;
import com.sportsmanager.core.MatchEvent;
import com.sportsmanager.core.Player;
import com.sportsmanager.core.Tactic;
import com.sportsmanager.core.Team;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class FootballMatch extends AbstractMatch {

    private List<Player> homeLineup;
    private List<Player> awayLineup;
    private Tactic homeTactic;
    private Tactic awayTactic;
    private Random random;

    public FootballMatch(Team homeTeam, Team awayTeam) {
        super(homeTeam, awayTeam);
        this.homeLineup = new ArrayList<>();
        this.awayLineup = new ArrayList<>();
        this.random = new Random();
    }

    @Override
    protected int getPeriodCount() {
        return 2;
    }

    @Override
    protected void simulatePeriod(int periodNumber) {

        if (periodNumber == 1) {
            homeLineup = new ArrayList<>(homeTeam.selectLineup());
            awayLineup = new ArrayList<>(awayTeam.selectLineup());
        }

        homeTactic = homeTeam.getTactic();
        awayTactic = awayTeam.getTactic();

        int homeAvg = calcAverageRating(homeLineup);
        int awayAvg = calcAverageRating(awayLineup);
        double ratingEdge = (homeAvg - awayAvg) / 100.0;

        double homeOff = (homeTactic != null) ? homeTactic.getOffensiveBonus() : 1.0;
        double homeDef = (homeTactic != null) ? homeTactic.getDefensiveBonus() : 1.0;
        double awayOff = (awayTactic != null) ? awayTactic.getOffensiveBonus() : 1.0;
        double awayDef = (awayTactic != null) ? awayTactic.getDefensiveBonus() : 1.0;

        int startMinute = (periodNumber - 1) * 45 + 1;
        int endMinute = periodNumber * 45;

        for (int minute = startMinute; minute <= endMinute; minute++) {

            double homeGoalChance = (0.015 + ratingEdge * 0.05) * homeOff / awayDef;
            homeGoalChance = Math.max(0.005, Math.min(0.08, homeGoalChance));

            if (random.nextDouble() < homeGoalChance) {
                Player scorer = pickRandom(homeLineup);
                if (scorer != null) {
                    homeScore++;
                    notifyObservers(new MatchEvent(
                            MatchEvent.EventType.GOAL, minute,
                            scorer.getName(), homeTeam.getName(), "Goal!"));
                }
            }

            double awayGoalChance = (0.015 - ratingEdge * 0.05) * awayOff / homeDef;
            awayGoalChance = Math.max(0.005, Math.min(0.08, awayGoalChance));

            if (random.nextDouble() < awayGoalChance) {
                Player scorer = pickRandom(awayLineup);
                if (scorer != null) {
                    awayScore++;
                    notifyObservers(new MatchEvent(
                            MatchEvent.EventType.GOAL, minute,
                            scorer.getName(), awayTeam.getName(), "Goal!"));
                }
            }

            if (random.nextDouble() < 0.02) {
                boolean isHome = random.nextBoolean();
                List<Player> deck = isHome ? homeLineup : awayLineup;
                Team side = isHome ? homeTeam : awayTeam;
                Player carded = pickRandom(deck);
                if (carded != null) {
                    notifyObservers(new MatchEvent(
                            MatchEvent.EventType.YELLOW_CARD, minute,
                            carded.getName(), side.getName(), "Yellow card!"));
                }
            }

            if (random.nextDouble() < 0.005) {
                boolean isHome = random.nextBoolean();
                List<Player> deck = isHome ? homeLineup : awayLineup;
                Team side = isHome ? homeTeam : awayTeam;
                Player carded = pickRandom(deck);
                if (carded != null) {
                    notifyObservers(new MatchEvent(
                            MatchEvent.EventType.RED_CARD, minute,
                            carded.getName(), side.getName(), "Red card! Player sent off."));
                    deck.remove(carded);
                }
            }
        }

        if (random.nextDouble() < 0.30) {
            substitutePlayer(homeLineup, homeTeam, endMinute - 5);
        }
        if (random.nextDouble() < 0.30) {
            substitutePlayer(awayLineup, awayTeam, endMinute - 5);
        }

        notifyObservers(MatchEvent.periodEnd(endMinute));

        for (Player p : homeLineup) {
            p.applyMatchFatigue();
        }
        for (Player p : awayLineup) {
            p.applyMatchFatigue();
        }

        for (Player p : homeTeam.getPlayers()) {
            if (!homeLineup.contains(p)) {
                p.recoverOneGame();
            }
        }
        for (Player p : awayTeam.getPlayers()) {
            if (!awayLineup.contains(p)) {
                p.recoverOneGame();
            }
        }
    }

    public void applyTacticChange(Team team, Tactic newTactic) {
        team.setTactic(newTactic);
    }

    @Override
    public void substitutePlayer(Team team, Player playerOut, Player playerIn) {
        List<Player> lineup = team.equals(homeTeam) ? homeLineup : awayLineup;
        if (lineup != null && lineup.remove(playerOut)) {
            lineup.add(playerIn);
        }
    }

    private void substitutePlayer(List<Player> lineup, Team team, int minute) {
        Player playerOut = null;
        int lowestRating = Integer.MAX_VALUE;
        for (Player p : lineup) {
            if (p.getOverallRating() < lowestRating) {
                lowestRating = p.getOverallRating();
                playerOut = p;
            }
        }

        List<Player> bench = new ArrayList<>();
        for (Player p : team.getPlayers()) {
            if (!lineup.contains(p) && p.isAvailable()) {
                bench.add(p);
            }
        }

        if (playerOut == null || bench.isEmpty()) {
            return;
        }

        Player playerIn = bench.get(random.nextInt(bench.size()));

        lineup.remove(playerOut);
        lineup.add(playerIn);
        notifyObservers(new MatchEvent(
                MatchEvent.EventType.SUBSTITUTION, minute,
                playerOut.getName(), team.getName(), playerIn.getName() + " came on"));
    }

    private int calcAverageRating(List<Player> lineup) {
        if (lineup == null || lineup.isEmpty()) return 50;
        int total = 0;
        for (Player p : lineup) {
            total += p.getOverallRating();
        }
        return total / lineup.size();
    }

    private Player pickRandom(List<Player> lineup) {
        if (lineup == null || lineup.isEmpty()) return null;
        return lineup.get(random.nextInt(lineup.size()));
    }
}
