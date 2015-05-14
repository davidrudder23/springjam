function Songs($scope, $http) {
    $http.get('/song').
        success(function(data) {
            $scope.songs = data;
        }).then(function(data) {
            $http.get('/song/0/seen').
                success(function (data) {
                    $scope.seenSongs = data;
                }).then(function (data) {
                    angular.forEach($scope.songs, function (song, idx) {
                        song.seen = false;

                        angular.forEach($scope.seenSongs, function (seenSong, songIdx) {
                            if (song.id == seenSong.id) {
                                song.seen=true;
                            }
                        })
                    });
                })
        });

    $scope.sortBy = 'seen';
    $scope.sortReverse = true;

    $scope.showSong = function(id) {
        $http.get("/song/"+id).
            success(function(data) {
                $scope.song = data;
            }).then(function() {
                console.log($scope.song);
                angular.forEach($scope.song.performances, function (performance, idx) {
                    console.log(performance);
                    console.log("Looking for concert "+performance.concert);
                    $http.get("/concert/" + performance.concert).
                        success(function (data) {
                            performance.concert = data;
                        });
                });
            });
    }
}