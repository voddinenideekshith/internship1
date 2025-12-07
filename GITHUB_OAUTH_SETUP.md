# GitHub OAuth Setup Guide

## Step 1: Create GitHub OAuth Application

1. Go to GitHub Settings: https://github.com/settings/developers
2. Click "New OAuth App"
3. Fill in the following details:

   **Application name:** Git Tasker
   
   **Homepage URL:** https://internship1.yellowdune-6b01a061.centralindia.azurecontainerapps.io
   
   **Authorization callback URL:** https://internship1.yellowdune-6b01a061.centralindia.azurecontainerapps.io/login/oauth2/code/github

4. Click "Register application"
5. You'll see your **Client ID** - copy it
6. Click "Generate a new client secret" and copy it

## Step 2: Add Credentials to Azure Container App

Set these environment variables in your Azure Container App:

```bash
SPRING_SECURITY_OAUTH2_CLIENT_REGISTRATION_GITHUB_CLIENT_ID=<your-client-id>
SPRING_SECURITY_OAUTH2_CLIENT_REGISTRATION_GITHUB_CLIENT_SECRET=<your-client-secret>
```

You can do this using Azure Portal or Azure CLI:

```bash
# Using Azure CLI
az containerapp update \
  --name internship1 \
  --resource-group rg-dev \
  --set-env-vars \
  SPRING_SECURITY_OAUTH2_CLIENT_REGISTRATION_GITHUB_CLIENT_ID='<your-client-id>' \
  SPRING_SECURITY_OAUTH2_CLIENT_REGISTRATION_GITHUB_CLIENT_SECRET='<your-client-secret>'
```

Or using Azure Portal:
1. Go to Container Apps in Azure Portal
2. Select "internship1"
3. Go to "Settings" → "Containers"
4. Edit the container and add the environment variables above
5. Click "Save" to deploy

## Step 3: Test the Integration

1. Navigate to: https://internship1.yellowdune-6b01a061.centralindia.azurecontainerapps.io
2. Click "Sign in with GitHub"
3. Authorize the application
4. You should be redirected to the task list with your GitHub profile displayed

## Features Enabled

✅ GitHub OAuth login
✅ User profile display (name + avatar)
✅ Logout functionality
✅ Guest access without login
✅ Task management for authenticated users

## Security Notes

- Never commit secrets to the repository
- Use environment variables for credentials
- For local development, update `application.properties` with test credentials (but don't commit)
- Use Azure Key Vault for production secrets

## Troubleshooting

If OAuth doesn't work:
1. Check that the callback URL matches exactly in GitHub app settings
2. Verify credentials are correctly set as environment variables
3. Check container logs: `az containerapp logs show --name internship1 --resource-group rg-dev`
4. Make sure you're accessing the HTTPS URL (not HTTP)
