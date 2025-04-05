from azul_env import AzulEnv

env = AzulEnv(player_count=2)

env.reset(seed=42)

for agent in env.agent_iter():
    observation, reward, termination, truncation, info = env.last()
    if termination or truncation:
        break
    action = env.action_space(agent).sample()
    env.step(action)

env.close()