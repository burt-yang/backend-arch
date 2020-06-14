local read_list_name = KEYS[1]

local thread_id = ARGV[1]
local capacity = tonumber(ARGV[2])
local time_out = tonumber(ARGV[3])

local list_length = tonumber(redis.call("LLEN", read_list_name))

local allowed = capacity >= list_length
if allowed then
  redis.call("LPUSH", read_list_name, thread_id)
  redis.call("EXPIRE", read_list_name, time_out)
  return thread_id
end

return nil