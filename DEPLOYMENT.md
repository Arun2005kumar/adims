# Deployment Guide - Railway.app

## Prerequisites
- GitHub account
- Railway.app account (free at https://railway.app)
- This repository pushed to GitHub

## Step-by-Step Deployment

### 1. Create Railway Account
1. Go to https://railway.app
2. Sign up with GitHub
3. Authorize Railway to access your repositories

### 2. Create a New Project
1. Click **"New Project"** in Railway dashboard
2. Select **"Deploy from GitHub"**
3. Select the repository: `Arun2005kumar/adims`
4. Select branch: `main`
5. Click **"Create"**

### 3. Configure Environment Variables
After Railway detects the Dockerfile:

1. Go to **Variables** tab
2. Add the following environment variables:

```
DB_HOST=postgresql://username:password@host:port
DB_PORT=5432
DB_NAME=antidoping_investigation
DB_USERNAME=adims_user
DB_PASSWORD=adims_pass
JWT_SECRET=c2VjcmV0LWtleS1mb3ItYW50aS1kb3BpbmctaW50ZWxsaWdlbmNlLXN5c3RlbS1jaGFuZ2UtdGhpcy1pbi1wcm9kdWN0aW9uLTEyMzQ1Njc4OTA=
JWT_EXPIRATION_MS=86400000
CORS_ALLOWED_ORIGINS=https://your-railway-url.railway.app,https://your-frontend-url.com
SERVER_PORT=8080
```

### 4. Add PostgreSQL Database
1. Click **"+ New"** button
2. Select **"Database"**
3. Choose **"PostgreSQL"**
4. Connect to your project
5. Railway will provide `DATABASE_URL` automatically

### 5. Configure Database in Railway
The `DATABASE_URL` will be automatically set. Update your variables:
```
DB_HOST=postgres.railway.internal
DB_PORT=5432
DB_NAME=railway
DB_USERNAME=postgres
DB_PASSWORD=(from DATABASE_URL)
```

### 6. Deploy
1. Railway automatically starts building
2. Watch the build logs in **"Deployments"** tab
3. Once successful, you'll get a public URL
4. Your app is live! 🚀

### 7. Access Your Application
```
Frontend: https://your-app.railway.app
API: https://your-app.railway.app/api
Login: admin / Admin@123
```

## Monitoring & Logs
- Go to **Deployments** → select active deployment
- Click **"View Logs"** to monitor in real-time
- Check **"Metrics"** for CPU, memory, disk usage

## Environment Variables Reference

| Variable | Default | Description |
|---|---|---|
| `DB_HOST` | localhost | PostgreSQL host |
| `DB_PORT` | 5432 | PostgreSQL port |
| `DB_NAME` | antidoping_investigation | Database name |
| `DB_USERNAME` | adims_user | Database user |
| `DB_PASSWORD` | adims_pass | Database password |
| `JWT_SECRET` | (see above) | JWT signing key |
| `JWT_EXPIRATION_MS` | 86400000 | Token expiration (24h) |
| `CORS_ALLOWED_ORIGINS` | http://localhost:* | Allowed CORS origins |
| `SERVER_PORT` | 8080 | Application port |

## Troubleshooting

### Build Failed
- Check build logs in Deployments
- Ensure Dockerfile is in root directory
- Verify Java version is 21

### Database Connection Error
- Verify `DATABASE_URL` environment variable
- Check PostgreSQL service is running
- Ensure credentials are correct

### Application Crashes
- Check logs for errors
- Verify all environment variables are set
- Check memory/CPU limits

## Auto-Deployment
Once connected:
- Any push to `main` branch automatically triggers deployment
- Wait ~5-10 minutes for build and deploy to complete
- Check Deployments tab for status

## Update Application
```bash
git add .
git commit -m "Update application"
git push origin main
# Railway automatically deploys!
```

## Support
- Railway Docs: https://docs.railway.app
- GitHub Issues: Create an issue in your repository
