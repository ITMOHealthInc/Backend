TOKEN='eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJhdWQiIj...'

# create / replace
curl -X POST http://localhost:5015/goals \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"goal_type":"weight_loss","activity_level":"medium","weekly_target":0.5,
       "calorie_goal":1500,"water_goal":3000,"steps_goal":10000,"bju_goal":"standard"}'

# read
curl -H "Authorization: Bearer $TOKEN" http://localhost:5015/goals

# update
curl -X PUT http://localhost:5015/goals \
  -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json" \
  -d '{"goal_type":"maintain_weight","activity_level":"low","weekly_target":0.0,
       "calorie_goal":2000,"water_goal":2500,"steps_goal":8000,"bju_goal":"balanced"}'

# delete
curl -X DELETE http://localhost:5015/goals -H "Authorization: Bearer $TOKEN"
