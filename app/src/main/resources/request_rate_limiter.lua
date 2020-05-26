local tokens_key = KEYS[1]
local timestamp_key = KEYS[2]
--redis.log(redis.LOG_WARNING, "tokens_key " .. tokens_key)

local rate = tonumber(ARGV[1])
local capacity = tonumber(ARGV[2])
local now = tonumber(ARGV[3])
local requested = tonumber(ARGV[4])

--计算填满桶需要多长时间
local fill_time = capacity/rate
-- 得到填满桶的2倍时间作为redis中key时效的时间，避免冗余太多无用的key
local ttl = math.floor(fill_time*2)
-- redis.log(redis.LOG_WARNING, "rate " .. ARGV[1])
-- redis.log(redis.LOG_WARNING, "capacity " .. ARGV[2])
-- redis.log(redis.LOG_WARNING, "now " .. ARGV[3])
-- redis.log(redis.LOG_WARNING, "requested " .. ARGV[4])
-- redis.log(redis.LOG_WARNING, "filltime " .. fill_time)
-- redis.log(redis.LOG_WARNING, "ttl " .. ttl)

-- 获取桶中剩余的令牌，如果桶是空的，就将他填满
local last_tokens = tonumber(redis.call("get", tokens_key))
if last_tokens == nil then
  last_tokens = capacity
end
--redis.log(redis.LOG_WARNING, "last_tokens " .. last_tokens)

--获取当前令牌桶最后的刷新时间，如果为空，则设置为0
local last_refreshed = tonumber(redis.call("get", timestamp_key))
if last_refreshed == nil then
  last_refreshed = 0
end
--redis.log(redis.LOG_WARNING, "last_refreshed " .. last_refreshed)

--计算最后一次刷新令牌到当前时间的时间差
local delta = math.max(0, now-last_refreshed)
--计算当前令牌数量，这个地方是最关键的地方，通过剩余令牌数 + 时间差内产生的令牌得到当前总令牌数量
local filled_tokens = math.min(capacity, last_tokens+(delta*rate))
-- 设置标识allowad接收当前令牌桶中的令牌数是否大于请求的令牌结果
local allowed = filled_tokens >= requested
local new_tokens = filled_tokens
local allowed_num = 0
-- 如果allowed为true，则将当前令牌数量重置为通中的令牌数 - 请求的令牌数，并且设置allowed_num标识为1
if allowed then
  new_tokens = filled_tokens - requested
  allowed_num = 1
end

--redis.log(redis.LOG_WARNING, "delta " .. delta)
--redis.log(redis.LOG_WARNING, "filled_tokens " .. filled_tokens)
--redis.log(redis.LOG_WARNING, "allowed_num " .. allowed_num)
--redis.log(redis.LOG_WARNING, "new_tokens " .. new_tokens)
-- 将当前令牌数量写回到redis中，并重置令牌桶的最后刷新时间
redis.call("setex", tokens_key, ttl, new_tokens)
redis.call("setex", timestamp_key, ttl, now)

-- 返回当前是否申请到了令牌，以及当前桶中剩余多少令牌
return {allowed_num, new_tokens}