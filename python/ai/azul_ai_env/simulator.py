from azul_env import AzulEnv
import time

env = AzulEnv(player_count=2)

env.reset()

# Render initial state
print("Initial state:")
env.render()
time.sleep(2)  # Give time for the plot to display

step_count = 0
for agent in env.agent_iter():
    observation, reward, termination, truncation, info = env.last()
    if termination or truncation:
        break
    action = env.action_space(agent).sample()
    print(f"Step {step_count}: Agent {agent} taking action {action}")
    env.step(action)
    
    # Render after step to show updated state
    env.render()
    time.sleep(0.1)  # Give time for the plot to display

print("Simulation completed!")
env.close()