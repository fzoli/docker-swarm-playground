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

# Force re-deploy opsdemo

```sh
docker service update --force $(docker service ls --filter name=opsdemo-stack_demo -q)
```

# Test

### Check communication

```sh
while true; do
  curl http://opsdemo.local:88/greeting
  echo ''
  sleep 0.2
done
```

### Check containers

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

# NVIDIA GPU

To use NVIDIA GPU, edit `/etc/docker/daemon.json`

```json
{
    "default-runtime": "nvidia",
    "runtimes": {
        "nvidia": {
            "args": [],
            "path": "nvidia-container-runtime"
        }
    }
}
```

Note that `default-runtime` will change the runtime to `nvidia` from `runc`, so each container will see each NVIDIA card by default.
You can limit accessible cards per container with env var: `NVIDIA_VISIBLE_DEVICES`

For example:
```
docker run --rm -e NVIDIA_VISIBLE_DEVICES= nvidia/cuda:12.4.1-base-ubuntu22.04 nvidia-smi
```

No need for `--gpus all` argument thanks to `default-runtime`.
