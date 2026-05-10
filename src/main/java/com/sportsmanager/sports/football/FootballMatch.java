package com.sportsmanager.sports.football;

import com.sportsmanager.core.AbstractMatch;
import com.sportsmanager.core.AbstractPlayer;
import com.sportsmanager.core.MatchEvent;
import com.sportsmanager.core.Player;
import com.sportsmanager.core.Tactic;
import com.sportsmanager.core.Team;

import java.util.ArrayList;
import java.util.List;
import java.util.LinkedHashSet;
import java.util.Random;
import java.util.Set;

public class FootballMatch extends AbstractMatch {

    private List<Player> homeLineup;
    private List<Player> awayLineup;
    private Tactic homeTactic;
    private Tactic awayTactic;
    private Random random;
    private Set<Player> homeMatchParticipants;
    private Set<Player> awayMatchParticipants;
    private Set<Player> homePeriodParticipants;
    private Set<Player> awayPeriodParticipants;

    public FootballMatch(Team homeTeam, Team awayTeam) {
        super(homeTeam, awayTeam);
        this.homeLineup = new ArrayList<>();
        this.awayLineup = new ArrayList<>();
        this.random = new Random();
        this.homeMatchParticipants = new LinkedHashSet<>();
        this.awayMatchParticipants = new LinkedHashSet<>();
        this.homePeriodParticipants = new LinkedHashSet<>();
        this.awayPeriodParticipants = new LinkedHashSet<>();
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
            homeMatchParticipants.clear();
            awayMatchParticipants.clear();
        }

        beginPeriodTracking();
        homeTactic = homeTeam.getTactic();
        awayTactic = awayTeam.getTactic();

        int homeAvg = calcAverageRating(homeLineup);
        int awayAvg = calcAverageRating(awayLineup);
        double ratingEdge = (homeAvg - awayAvg) / 100.0;

        double homeOff = toMultiplier(homeTactic != null ? homeTactic.getOffensiveBonus() : 0.0);
        double homeDef = toMultiplier(homeTactic != null ? homeTactic.getDefensiveBonus() : 0.0);
        double awayOff = toMultiplier(awayTactic != null ? awayTactic.getOffensiveBonus() : 0.0);
        double awayDef = toMultiplier(awayTactic != null ? awayTactic.getDefensiveBonus() : 0.0);

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

        for (Player p : homePeriodParticipants) {
            p.applyMatchFatigue();
        }
        for (Player p : awayPeriodParticipants) {
            p.applyMatchFatigue();
        }

        if (periodNumber == getPeriodCount()) {
            recoverInactivePlayers(homeTeam, homeMatchParticipants);
            recoverInactivePlayers(awayTeam, awayMatchParticipants);
            maybeInjurePlayer(homeLineup, homeTeam, endMinute, 0.08, 1, 3);
            maybeInjurePlayer(awayLineup, awayTeam, endMinute, 0.08, 1, 3);
        }
    }

    public void applyTacticChange(Team team, Tactic newTactic) {
        if (team != null && newTactic != null) {
            team.setTactic(newTactic);
        }
    }

    @Override
    public void substitutePlayer(Team team, Player playerOut, Player playerIn) {
        if (team == null || playerOut == null || playerIn == null) {
            return;
        }
        if (!team.equals(homeTeam) && !team.equals(awayTeam)) {
            return;
        }

        List<Player> roster = team.getPlayers();
        if (!roster.contains(playerOut) || !roster.contains(playerIn)) {
            return;
        }

        List<Player> lineup = team.equals(homeTeam) ? homeLineup : awayLineup;
        if (lineup != null && lineup.remove(playerOut) && !lineup.contains(playerIn)) {
            lineup.add(playerIn);
            registerParticipation(team, playerOut);
            registerParticipation(team, playerIn);
        }
    }

    private void substitutePlayer(List<Player> lineup, Team team, int minute) {
        if (lineup == null || team == null) {
            return;
        }

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
        registerParticipation(team, playerOut);
        registerParticipation(team, playerIn);
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

    private void recoverInactivePlayers(Team team, Set<Player> participants) {
        Set<Player> playedPlayers = participants != null ? participants : Set.of();
        for (Player player : team.getPlayers()) {
            if (player != null && !playedPlayers.contains(player)) {
                player.recoverOneGame();
            }
        }
    }

    private void beginPeriodTracking() {
        homePeriodParticipants = new LinkedHashSet<>();
        awayPeriodParticipants = new LinkedHashSet<>();
        registerLineupParticipation(homeTeam, homeLineup);
        registerLineupParticipation(awayTeam, awayLineup);
    }

    private void registerLineupParticipation(Team team, List<Player> lineup) {
        if (lineup == null) {
            return;
        }

        for (Player player : lineup) {
            registerParticipation(team, player);
        }
    }

    private void registerParticipation(Team team, Player player) {
        if (team == null || player == null) {
            return;
        }

        if (team.equals(homeTeam)) {
            if (homeMatchParticipants == null) {
                homeMatchParticipants = new LinkedHashSet<>();
            }
            if (homePeriodParticipants == null) {
                homePeriodParticipants = new LinkedHashSet<>();
            }
            homeMatchParticipants.add(player);
            homePeriodParticipants.add(player);
        } else if (team.equals(awayTeam)) {
            if (awayMatchParticipants == null) {
                awayMatchParticipants = new LinkedHashSet<>();
            }
            if (awayPeriodParticipants == null) {
                awayPeriodParticipants = new LinkedHashSet<>();
            }
            awayMatchParticipants.add(player);
            awayPeriodParticipants.add(player);
        }
    }

    private void maybeInjurePlayer(List<Player> lineup,
                                   Team team,
                                   int minute,
                                   double chance,
                                   int minGames,
                                   int maxGames) {
        if (lineup == null || lineup.isEmpty() || random.nextDouble() >= chance) {
            return;
        }

        Player victim = pickRandom(lineup);
        if (!(victim instanceof AbstractPlayer abstractPlayer)) {
            return;
        }

        int games = minGames + random.nextInt(Math.max(1, maxGames - minGames + 1));
        abstractPlayer.injure(games);
        notifyObservers(new MatchEvent(
                MatchEvent.EventType.INJURY,
                minute,
                victim.getName(),
                team.getName(),
                victim.getName() + " will miss " + games + " game(s)"
        ));
    }

    private double toMultiplier(double bonus) {
        return Math.max(0.1, 1.0 + bonus);
    }
}
