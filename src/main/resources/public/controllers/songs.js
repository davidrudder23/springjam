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
}