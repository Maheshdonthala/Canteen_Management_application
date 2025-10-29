# Deploying to Render (quick guide)

This project is ready for deployment to Render using Docker. Follow the steps below.

1) Push repository to GitHub (if not already)
   - Render will connect to GitHub and build from the repository.

2) Add environment variables in your Render service settings (or edit `render.yaml`):
   - Key: `SPRING_DATA_MONGODB_URI`
     Value: `mongodb+srv://<user>:<pass>@<cluster>.mongodb.net/canteenDB?retryWrites=true&w=majority`
   - (Optional) `SPRING_PROFILES_ACTIVE=prod`

3) Create a new Web Service on Render
   - Environment: Docker
   - Connect the GitHub repo and pick branch `main`.
   - Render will use the `Dockerfile` at the repository root to build the image.

4) Build & Start
   - Build command: (Dockerfile does the building)
   - Start command: `java -Dserver.port=$PORT -jar target/canteen-management-system-0.0.1-SNAPSHOT.jar`
   - The Dockerfile sets the `PORT` env var and runs the jar; Render provides `$PORT`.

5) Health check and logs
   - Health path: `/actuator/health`
   - Check deploy logs for build output. If Mongo cannot be reached, ensure Atlas allows access from Render.

6) After deploy
   - Open the service URL and visit `/login` to access the app.
   - Use the seeded admin user (`admin` / `guest`) if you seeded data into your Atlas DB previously.

If you want I can also add a tiny GitHub Action or CI file to auto-deploy on push, or create a `render.yaml` with your actual secret values (not recommended to commit secrets). Let me know which you'd prefer.
