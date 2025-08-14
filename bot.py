import os
import discord
from discord.ext import commands, tasks
from discord import app_commands
from datetime import datetime, timedelta
import pytz
from dotenv import load_dotenv

# Load environment variables
load_dotenv()
TOKEN = os.getenv("DISCORD_TOKEN")
CHANNEL_ID = int(os.getenv("CHANNEL_ID_FOR_WEEKLY", "0"))
GUILD_ID = os.getenv("GUILD_ID")

# NFL Regular Season End Date (adjust if needed)
SEASON_END = datetime(2025, 1, 6, tzinfo=pytz.timezone("America/New_York"))

# In-memory bet store
bets = {}

intents = discord.Intents.default()
intents.message_content = True
bot = commands.Bot(command_prefix="!", intents=intents)

# ----- Slash Commands -----
@bot.event
async def on_ready():
    print(f"Logged in as {bot.user}")
    try:
        if GUILD_ID:
            guild = discord.Object(id=int(GUILD_ID))
            await bot.tree.sync(guild=guild)
        else:
            await bot.tree.sync()
        print("Slash commands synced.")
    except Exception as e:
        print(f"Error syncing commands: {e}")
    weekly_update.start()

@bot.tree.command(name="addbet", description="Add a new bet")
@app_commands.describe(
    bet_name="Name of the bet",
    participants="Tag the participants (@user1 @user2...)",
    snap_limit="Minimum snap % required for counting (e.g., 25)"
)
async def addbet(interaction: discord.Interaction, bet_name: str, participants: str, snap_limit: int = 0):
    if datetime.now(pytz.timezone("America/New_York")) > SEASON_END:
        await interaction.response.send_message("The NFL regular season is over. No new bets allowed.", ephemeral=True)
        return

    bets[bet_name] = {
        "participants": participants.split(),
        "snap_limit": snap_limit,
        "status": "open",
        "winner": None
    }
    await interaction.response.send_message(f"✅ Bet **{bet_name}** added with participants {participants} and snap limit {snap_limit}%.")

@bot.tree.command(name="editbet", description="Edit an existing bet")
@app_commands.describe(
    bet_name="Name of the bet to edit",
    participants="Updated participant list (optional)",
    snap_limit="Updated snap % limit (optional)",
    status="Updated status: open/closed (optional)",
    winner="Winner username or ID if closed (optional)"
)
async def editbet(interaction: discord.Interaction, bet_name: str, participants: str = None, snap_limit: int = None, status: str = None, winner: str = None):
    if bet_name not in bets:
        await interaction.response.send_message(f"❌ Bet **{bet_name}** not found.", ephemeral=True)
        return

    if participants:
        bets[bet_name]["participants"] = participants.split()
    if snap_limit is not None:
        bets[bet_name]["snap_limit"] = snap_limit
    if status:
        bets[bet_name]["status"] = status
    if winner:
        bets[bet_name]["winner"] = winner

    await interaction.response.send_message(f"✏ Bet **{bet_name}** updated.")

@bot.tree.command(name="listbets", description="List all bets and their status")
async def listbets(interaction: discord.Interaction):
    if not bets:
        await interaction.response.send_message("No bets available.")
        return

    msg = "**Current Bets:**\n"
    for name, data in bets.items():
        msg += f"- **{name}**: {data['status']} | Participants: {' '.join(data['participants'])} | Snap% ≥ {data['snap_limit']}"
        if data["winner"]:
            msg += f" | Winner: {data['winner']}"
        msg += "\n"

    await interaction.response.send_message(msg)

# ----- Weekly Standings -----
@tasks.loop(hours=24)
async def weekly_update():
    now = datetime.now(pytz.timezone("America/New_York"))
    if now.weekday() == 1 and now.hour == 12:  # Tuesday noon ET
        if datetime.now(pytz.timezone("America/New_York")) > SEASON_END:
            channel = bot.get_channel(CHANNEL_ID)
            if channel:
                await channel.send("🏈 The NFL regular season is over. Final standings locked.")
            weekly_update.stop()
            return

        channel = bot.get_channel(CHANNEL_ID)
        if channel:
            standings_msg = "**Weekly Standings:**\n"
            for name, data in bets.items():
                standings_msg += f"- {name}: {data['status']}"
                if data["winner"]:
                    standings_msg += f" | Winner: {data['winner']}"
                standings_msg += "\n"
            await channel.send(standings_msg)

@weekly_update.before_loop
async def before_weekly():
    await bot.wait_until_ready()

bot.run(TOKEN)
