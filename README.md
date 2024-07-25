# Sushi counter

Tired of inefficient orders in sushi restaurants without a tablet? With a room system, this project aims to make it easy for you and your friends to make orders in sushi restaurants!

> [!NOTE]
> This is still a work in progress. However, it is fully functional as of now. More quality-of-life improvements and features will be added soon.

## What is it?

The simplest system to order together with friends at sushi restaurants that do not provide you with a tablet:

1. Choose a room name which only you and your friends know. 
2. Every one of you enter that room name in the homepage.
3. Click on the "Create or join room" and boom! You are in.

Since sushi restaurants are notorious to have dishes identified with numbers, you get a list of a bunch of them, so you can start ordering right away!

When you are done, click on "Review" to see the orders you've made. I know you change your mind a lot, so you can still edit them.

When you are *really* done, click on "See room totals" to see all the orders of your friends in the room, so that you can make one single (yet GIANT) order.

## I want to try it now!

Go ahead: https://www.edotm.net/sushi-counter/. If you encounter a bug or a vulnerability, please contact me.


## I want to run it on my computer!

> [!WARNING]
> This setup is for development only. Do NOT use this in production. I will update this readme as soon as possible with instructions on how to build it for production.

Great news! You only need Docker installed, and you are good to go. Before you start, set the environment variable `SC_SSK` for the cookie signing key. If you are unsure on what to use, just generate a new random one:
```Shell
export SC_SSK=$(openssl rand -hex 32)
```
Now, you are ready!
```Shell
docker compose up -d
```
To stop it, just run
```Shell
docker compose down
```
> [!TIP]
> Remember that to run `docker compose` commands you need to be on the same directory of the `docker-compose.yml` file!
