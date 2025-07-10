# Build opsdemo image

```sh
docker build . -t opsdemo:latest
```

# Prepare Docker swarm

```
docker swarm init --advertise-addr 127.0.0.1
```

Only for local playground. On a real env the public IP must be used instead of localhost.

# Prepare Docker network

```sh
docker network create --driver overlay --attachable traefik-public
```

# Deploy Traefik

```sh
docker stack deploy -c traefik-compose.yaml traefik-stack --detach=true
```

Add this line to hosts file:
`127.0.0.1 opsdemo.local`

# Deploy opsdemo

```sh
set -o allexport
source ./.env
set +o allexport

docker stack deploy -c compose.yaml opsdemo-stack --detach=true --prune
```

Docker swarm does not use `.env` file, so we export the env vars into the shell session before executing deploy command.

# Test

```sh
while true; do
  curl http://opsdemo.local:88/greeting
  echo ''
  sleep 0.2
done
```

```sh
while true; do
  docker ps | grep opsdemo
  echo '----'
  sleep 1
done
```

# Cleanup

```sh
docker stack rm traefik-stack
docker stack rm opsdemo-stack
docker network rm traefik-public
```

Remove `opsdemo.local` from hosts file.

Delete swarm:
```sh
docker swarm leave --force
```
