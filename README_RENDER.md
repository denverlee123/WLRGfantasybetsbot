# WLRG Discord NFL Bets Bot — Render One-Click

## Deploy on Render
1. Upload this repo to GitHub.
2. Open: https://render.com/deploy?repo=<YOUR_REPO_URL>
3. Set env vars in the Worker:
   - DISCORD_TOKEN
   - CHANNEL_ID_FOR_WEEKLY
   - GUILD_ID (optional)
4. Deploy. Bot will auto-post standings every Tuesday.

## Local Run
pip install -r requirements.txt
cp .env.example .env
python bot.py
